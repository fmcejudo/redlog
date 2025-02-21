package io.github.fmcejudo.redlogs.healthcheck.card;

import io.github.fmcejudo.redlogs.card.AbstractCardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class HealthCheckQueryRequest extends AbstractCardQueryRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  public HealthCheckQueryRequest(CardQuery cardQuery, CardMetadata metadata) {
    super(cardQuery, metadata);
    this.cardQuery = cardQuery;
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
