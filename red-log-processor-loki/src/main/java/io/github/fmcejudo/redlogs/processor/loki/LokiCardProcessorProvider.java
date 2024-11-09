package io.github.fmcejudo.redlogs.processor.loki;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.processor.CardProcessorProvider;

public class LokiCardProcessorProvider implements CardProcessorProvider  {

  public CardProcessor createProcessor(Map<String,String> details) {
    return new LokiCardProcessor(details);
  }
}
