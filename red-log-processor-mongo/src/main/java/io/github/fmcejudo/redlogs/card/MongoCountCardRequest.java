package io.github.fmcejudo.redlogs.card;

import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class MongoCountCardRequest extends AbstractCardQueryRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  public MongoCountCardRequest(final CardQuery cardQuery, CardMetadata cardMetadata) {
    super(cardQuery, cardMetadata);
    this.cardQuery = cardQuery;
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return c -> {
    };
  }

  public String collection() {
    return cardQuery.properties().get("collection");
  }

  public String query() {
    return cardQuery.properties().get("query");
  }

  public String[] fields() {
    return Stream.of(cardQuery.properties().getOrDefault("fields", "").split(",")).map(String::trim).toArray(String[]::new);
  }
}
