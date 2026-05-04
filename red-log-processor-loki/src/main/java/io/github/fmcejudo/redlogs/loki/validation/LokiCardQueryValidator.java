package io.github.fmcejudo.redlogs.loki.validation;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LokiCardQueryValidator implements CardQueryValidator {

  private static final Logger log = LoggerFactory.getLogger(LokiCardQueryValidator.class);

  @Override
  public CardQueryValidation validate(CardQueryRequest cardQueryRequest) {
    if (cardQueryRequest instanceof LokiCountCardRequest lccr) {
      log.info("[ count-loki-card-request-validation ] - validating {}", lccr.id());
    } else if (cardQueryRequest instanceof LokiSummaryCardRequest lscr) {
      log.info("[ summary-loki-card-request-validation ] - validating {}", lscr.id());
    }
    return CardQueryValidation.success();
  }

  /*
  private void validateCards(final CardFile cardFile, final CardContext cardContext) {
    CardValidation cardValidation = cardValidator.validateOn(cardFile, cardContext);
    if (cardValidation.isFailure()) {
      throw new CardException(String.join(";", cardValidation.errors()));
    }
  }

  private List<CardQueryRequest> readQueries(final CardContext cardContext, final CardFile cardFile) {
    return cardFile.queries().stream().map(cq -> convertToQueryRequest(cq, cardFile, cardContext)).toList();
  }


  private CardQueryRequest convertToQueryRequest(CardQuery cardQuery,
      final CardFile cardFile, final CardContext cardContext) {

    UnaryOperator<String> queryReplaceFn = buildResolvedQuery(cardFile, cardContext);
    return null;
  }

  private UnaryOperator<String> buildResolvedQuery(final CardFile cardFile, final CardContext cardContext) {
    return q -> {
      String query = q;
      if (cardFile.range() != null) {
        query = query.replace("_RANGE_", cardFile.range());
      }
      StringSubstitutor substitutor = new StringSubstitutor(cardContext.parameters(), "<", ">");
      return substitutor.replace(query.replace("<common_query>", cardFile.commonQuery()));
    };
  }

  private String findRange(final String range, final CardContext cardContext) {
    if (range.startsWith("<") && range.endsWith(">")) {
      return cardContext.parameters().get(range.substring(1, range.length() - 1));
    }
    return range;
  }
 */
}
