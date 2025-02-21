package io.github.fmcejudo.redlogs.card;

import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class MongoListCardRequest extends AbstractCardQueryRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  public MongoListCardRequest(final CardQuery cardQuery, final CardMetadata cardMetadata) {
    super(cardQuery, cardMetadata);
    this.cardQuery = cardQuery;
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return c -> {
    };
  }

  public String[] fields() {
    return Stream.of(cardQuery.properties().get("fields").split(",")).map(String::trim).toArray(String[]::new);
  }

  public String collection() {
    return cardQuery.properties().get("collection");
  }

  public String query() {
    return cardQuery.properties().get("query");
  }
}
