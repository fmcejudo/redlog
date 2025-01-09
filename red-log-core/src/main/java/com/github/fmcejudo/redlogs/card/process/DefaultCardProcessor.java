package com.github.fmcejudo.redlogs.card.process;

import java.util.HashMap;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;

class DefaultCardProcessor implements CardProcessor {

  private final Map<String, CardQueryProcessor> cardQueryProcessorMap;

  DefaultCardProcessor() {
    this.cardQueryProcessorMap = new HashMap<>();
  }

  @Override
  public CardQueryResponse process(CardQueryRequest cardQueryRequest) {
    return cardQueryProcessorMap.get(cardQueryRequest.processor()).process(cardQueryRequest);
  }

  @Override
  public boolean register(String key, CardQueryProcessor cardQueryProcessor) {
    if (cardQueryProcessorMap.containsKey(key)) {
      return false;
    }
    cardQueryProcessorMap.putIfAbsent(key, cardQueryProcessor);
    return true;
  }

  @Override
  public boolean deregister(String key) {
    if (cardQueryProcessorMap.containsKey(key)) {
      cardQueryProcessorMap.remove(key);
      return true;
    }
    return false;
  }
}
