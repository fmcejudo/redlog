package io.github.fmcejudo.redlogs.processor.loki;

import java.util.Map;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.processor.CardProcessorProvider;

public class LokiCardProcessorProvider implements CardProcessorProvider  {

  public CardProcessor createProcessor(Map<String,String> details) {
    Map<String, String> lokiDetails = details.entrySet().stream()
        .filter(e -> e.getKey().startsWith("loki."))
        .map(e -> Map.entry(e.getKey().replace("loki.", ""), e.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return new LokiCardProcessor(lokiDetails);
  }

  @Override
  public String type() {
    return "LOKI";
  }
}
