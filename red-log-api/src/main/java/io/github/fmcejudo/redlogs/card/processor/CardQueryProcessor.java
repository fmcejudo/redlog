package io.github.fmcejudo.redlogs.card.processor;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;

public interface CardQueryProcessor {

  public abstract CardQueryResponse process(CardQueryRequest cardQueryRequest);

}
