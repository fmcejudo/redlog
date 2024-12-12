package com.github.fmcejudo.redlogs.card.process;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;

import io.github.fmcejudo.redlogs.card.processor.CardProcessor;

public final class CardProcessorFactory implements Closeable {

  private final Map<String, CardProcessor> cardProcessorMap;

  CardProcessorFactory(final Map<String, CardProcessor> cardProcessorMap) {
    this.cardProcessorMap = cardProcessorMap;
  }

  public CardProcessor ofType(final String type) {

    return Optional.ofNullable(cardProcessorMap.get("LOKI"))
        .orElseThrow(() -> new IllegalStateException("type %s is not registered".formatted(type)));
  }

  public void close() {
    cardProcessorMap.forEach((ignore, processor) -> processor.close());
  }

}
