package com.github.fmcejudo.redlogs.card.validator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.NumberUtils;

public class DefaultCardValidator implements CardValidator {

  private final CardValidator cardValidator;

  DefaultCardValidator() {
    this.cardValidator = CardValidator.validate(new ParameterValidator())
        .thenValidate(new RangeValidator())
        .thenValidate(new TimeValidator())
        .thenValidate(new ProcessorValidator());
  }

  @Override
  public CardValidation validateOn(CardFile cardFile, CardContext cardContext) {
    return cardValidator.validateOn(cardFile, cardContext);
  }
}

class RangeValidator implements CardValidator {

  private static final int THREE_DAYS = 24 * 3;

  @Override
  public CardValidation validateOn(final CardFile cardFile, final CardContext cardContext) {
    CardValidation cardValidation = validateRangeIsNotNull(cardFile);
    if (cardValidation.isFailure()) {
      return cardValidation;
    }
    String range = selectRange(cardFile, cardContext);
    return validateTimeRange(range);
  }

  private CardValidation validateRangeIsNotNull(CardFile cardFile) {
    if (cardFile.range() == null) {
      return CardValidation.invalid("range property on card must not be null");
    }
    return CardValidation.valid();
  }

  private String selectRange(final CardFile cardFile, final CardContext cardContext) {
    String range = cardFile.range();
    if (range.startsWith("<") && range.endsWith(">")) {
      String rangeVariable = range.substring(1, range.length() - 1);
      return cardContext.parameters().get(rangeVariable);
    }
    return range;
  }

  private CardValidation validateTimeRange(final String range) {
    if (range.endsWith("m")) {
      return validateAmountOfTime(range, t -> t > 0 && t <= 60 * THREE_DAYS);
    } else if (range.endsWith("h")) {
      return validateAmountOfTime(range, t -> t > 0 && t <= THREE_DAYS);
    }
    return CardValidation.invalid("valid units for range are 'm' (minutes) or 'h' (hours)");
  }

  private CardValidation validateAmountOfTime(final String range, IntPredicate validateAmountOfTime) {
    String amount = range.substring(0, range.length() - 1);
    int timeAmount;
    try {
      timeAmount = NumberUtils.parseNumber(amount, Integer.class);
    } catch (RuntimeException exception) {
      return CardValidation.invalid("it could not parse amount of time");
    }

    if (validateAmountOfTime.test(timeAmount)) {
      return CardValidation.valid();
    }
    return CardValidation.invalid("max amount of time in range are three days");
  }
}

class TimeValidator implements CardValidator {

  @Override
  public CardValidation validateOn(CardFile cardFile, CardContext cardContext) {
    if (cardFile.time() == null) {
      return CardValidation.invalid(
          "time key is required in card, to execute query with startTime or time as provided"
      );
    }
    return CardValidation.valid();
  }
}

class ParameterValidator implements CardValidator {

  public CardValidation validateOn(final CardFile cardFile, final CardContext cardContext) {

    Map<String, String> parameters = cardContext.parameters();
    if (cardFile.parameters() == null || cardFile.parameters().isEmpty()) {
      return CardValidation.valid();
    }
    CardValidation unknownParams = validateUnknownParams(cardFile, parameters);
    CardValidation extraParams = validateExtraParams(cardFile, parameters);
    return unknownParams.then(extraParams);
  }

  private CardValidation validateUnknownParams(CardFile cardFile, Map<String, String> parameters) {
    List<String> unknownParams = cardFile.parameters().stream()
        .filter(Predicate.not(parameters::containsKey))
        .toList();

    if (!unknownParams.isEmpty()) {
      return CardValidation.invalid("parameters '%s' not found in parameter map".formatted(unknownParams));
    }
    return CardValidation.valid();
  }

  private CardValidation validateExtraParams(CardFile cardFile, Map<String, String> parameters) {
    List<String> cardFileParams = cardFile.parameters();
    Set<String> contextParameters = parameters.keySet();
    List<String> extraParams = contextParameters.stream().filter(Predicate.not(cardFileParams::contains)).toList();
    if (extraParams.isEmpty()) {
      return CardValidation.valid();
    }
    return CardValidation.invalid(
        "context parameter defines extra params: %s".formatted(String.join(",", extraParams))
    );
  }

}

class ProcessorValidator implements CardValidator {

  @Override
  public CardValidation validateOn(CardFile cardFile, CardContext cardContext) {
    return cardFile.queries().stream().map(this::validateProcessorInCardQuery).reduce(CardValidation::then)
        .orElseGet(CardValidation::valid);
  }

  private CardValidation validateProcessorInCardQuery(CardQuery cardQuery) {
    if (StringUtils.isBlank(cardQuery.processor())) {
      return CardValidation.invalid("card query with id " + cardQuery.id() + " needs to define processor");
    }
    return CardValidation.valid();
  }
}