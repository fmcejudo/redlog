package com.github.fmcejudo.redlogs.card.process;

import java.util.function.Function;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import org.apache.commons.lang3.NotImplementedException;

@FunctionalInterface
public interface CardProcessor extends Function<CardQueryRequest, CardQueryResponse> {

  default CardQueryResponse apply(CardQueryRequest cardQueryRequest) {
    return this.process(cardQueryRequest);
  }

  CardQueryResponse process(CardQueryRequest cardQueryRequest);

  default boolean register(String key, CardQueryProcessor cardQueryProcessor) {
    throw new NotImplementedException();
  }

  default boolean deregister(String key) {
    throw new NotImplementedException();
  }

}
