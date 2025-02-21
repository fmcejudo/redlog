package io.github.fmcejudo.redlogs.healthcheck.card;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class HealthCheckQueryRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  private final CardMetadata metadata;

  public HealthCheckQueryRequest(CardQuery cardQuery, CardMetadata metadata) {
    this.cardQuery = cardQuery;
    this.metadata = metadata;
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
    return metadata().executionId();
  }

  @Override
  public String processor() {
    return cardQuery.processor();
  }

  @Override
  public CardMetadata metadata() {
    return metadata;
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return c -> {
    };
  }

  public String url() {
    return cardQuery.properties().get("url");
  }

  public String jsonPath() {
    return cardQuery.properties().get("jsonPath");
  }
}
