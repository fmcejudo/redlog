package io.github.fmcejudo.redlogs.card;

import java.util.List;
import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class MongoCountCardRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  private final CardMetadata cardMetadata;

  public MongoCountCardRequest(final CardQuery cardQuery, CardMetadata cardMetadata) {
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

  public String collection() {
    return cardQuery.properties().get("collection");
  }

  public String query() {
    return cardQuery.properties().get("query");
  }

  public List<String> fields() {
    return Stream.of(cardQuery.properties().getOrDefault("fields", "").split(",")).map(String::trim).toList();
  }
}
