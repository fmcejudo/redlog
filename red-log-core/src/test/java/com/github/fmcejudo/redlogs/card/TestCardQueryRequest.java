package com.github.fmcejudo.redlogs.card;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class TestCardQueryRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  private final CardMetadata cardMetadata;

  public TestCardQueryRequest(CardQuery cardQuery, CardMetadata cardMetadata) {
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
    return null;
  }

  @Override
  public CardMetadata metadata() {
    return cardMetadata;
  }

  @Override
  public String processor() {
    return cardQuery.processor();
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return null;
  }
}
