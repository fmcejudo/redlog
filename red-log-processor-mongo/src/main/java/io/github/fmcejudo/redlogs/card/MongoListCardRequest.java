package io.github.fmcejudo.redlogs.card;

import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class MongoListCardRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  private final CardMetadata cardMetadata;

  public MongoListCardRequest(final CardQuery cardQuery, final CardMetadata cardMetadata) {
    this.cardQuery = cardQuery;
    this.cardMetadata = cardMetadata;
  }

  @Override
  public String id() {
    return cardQuery.id();
  }

  @Override
  public String description() {
    return cardQuery.description();
  }

  @Override
  public String executionId() {
    return cardMetadata.executionId();
  }

  @Override
  public String processor() {
    return cardQuery.processor();
  }

  @Override
  public CardMetadata metadata() {
    return cardMetadata;
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
