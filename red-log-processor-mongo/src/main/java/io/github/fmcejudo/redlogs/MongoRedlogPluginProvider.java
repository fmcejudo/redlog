package io.github.fmcejudo.redlogs;

import java.util.Map;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import io.github.fmcejudo.redlogs.converter.MongoCardQueryConverter;
import io.github.fmcejudo.redlogs.processor.MongoCardQueryProcessor;

public class MongoRedlogPluginProvider implements RedlogPluginProvider {

  @Override
  public CardQueryProcessor createProcessor(Map<String, String> details) {
    Map<String, String> mongoDetails = details.entrySet().stream()
        .filter(e -> e.getKey().startsWith("mongo."))
        .map(e -> Map.entry(e.getKey().replace("mongo.", ""), e.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return MongoCardQueryProcessor.createProcessor(mongoDetails);
  }

  @Override
  public CardQueryConverter createCardQueryConverter() {
    return MongoCardQueryConverter.createInstance();
  }

  @Override
  public CardQueryValidator createCardQueryValidator() {
    return c -> {};
  }

  @Override
  public String type() {
    return "MONGO";
  }
}
