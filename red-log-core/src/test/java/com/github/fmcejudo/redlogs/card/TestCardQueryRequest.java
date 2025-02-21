package com.github.fmcejudo.redlogs.card;

import io.github.fmcejudo.redlogs.card.AbstractCardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class TestCardQueryRequest extends AbstractCardQueryRequest implements CardQueryRequest {

  public TestCardQueryRequest(CardQuery cardQuery, CardMetadata cardMetadata) {
    super(cardQuery, cardMetadata);
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return null;
  }
}
