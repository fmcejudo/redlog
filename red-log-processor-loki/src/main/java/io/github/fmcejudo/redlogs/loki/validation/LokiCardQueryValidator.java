package io.github.fmcejudo.redlogs.loki.validation;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class LokiCardQueryValidator implements CardQueryValidator {

  @Override
  public void validate(CardQueryRequest cardQueryRequest) {

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
