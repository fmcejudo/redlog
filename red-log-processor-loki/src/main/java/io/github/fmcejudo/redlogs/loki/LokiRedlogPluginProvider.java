package io.github.fmcejudo.redlogs.loki;

import java.util.Map;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import io.github.fmcejudo.redlogs.loki.converter.LokiCardQueryConverter;
import io.github.fmcejudo.redlogs.loki.processor.LokiCardQueryProcessor;

public class LokiRedlogPluginProvider implements RedlogPluginProvider {

  public CardQueryProcessor createProcessor(Map<String,String> details) {
    Map<String, String> lokiDetails = details.entrySet().stream()
        .filter(e -> e.getKey().startsWith("loki."))
        .map(e -> Map.entry(e.getKey().replace("loki.", ""), e.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return LokiCardQueryProcessor.withLokiConnectionDetails(lokiDetails);
  }

  @Override
  public CardQueryValidator createCardQueryValidator() {
    return cardQueryRequest -> {};
  }

  @Override
  public CardQueryConverter createCardQueryConverter() {
    return LokiCardQueryConverter.createInstance();
  }

  @Override
  public String type() {
    return "LOKI";
  }
}
