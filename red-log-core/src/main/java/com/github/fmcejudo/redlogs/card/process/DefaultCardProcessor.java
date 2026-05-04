package com.github.fmcejudo.redlogs.card.process;

import java.util.HashMap;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
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
    String processor = cardQueryRequest.processor();
    if (processor == null || processor.isBlank()) {
      throw new CardExecutionException("a processor is required in each card query to identify the query runner");
    }
    CardQueryProcessor cardQueryProcessor = cardQueryProcessorMap.get(processor);
    if (cardQueryProcessor == null) {
      throw new CardExecutionException("it found a query with a processor which is not registered: %s".formatted(processor));
    }

    return cardQueryProcessor.process(cardQueryRequest);
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
