package io.github.fmcejudo.redlogs.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;

public interface MongoCardQueryProcessor extends CardQueryProcessor {

  public static CardQueryProcessor createProcessor(final Map<String, String> connectionDetails) {

    Map<String, MongoConnectionProperties> mongoConnectionPropertiesMap = parse(connectionDetails);
    MongoTemplateFactory mongoTemplateFactory = MongoTemplateFactory.init(mongoConnectionPropertiesMap);
    MongoCountCardProcessor mongoCountCardProcessor = new MongoCountCardProcessor(mongoTemplateFactory);
    MongoListCardProcessor mongoListCardProcessor = new MongoListCardProcessor(mongoTemplateFactory);
    return cardQueryRequest -> {
      try {
        return processCardRequest(cardQueryRequest, mongoCountCardProcessor, mongoListCardProcessor);
      } catch (Exception e) {
        return CardQueryResponse.from(cardQueryRequest).failure(e.getMessage());
      }
    };
  }

  private static Map<String, MongoConnectionProperties> parse(Map<String, String> mongoProperties) {
    Map<String, Map<String, String>> connectionDetailsByReference = groupConnectionDetailsByReference(mongoProperties);
    return connectionDetailsByReference.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, e -> MongoConnectionProperties.from(e.getValue())));
  }

  private static Map<String, Map<String, String>> groupConnectionDetailsByReference(Map<String, String> mongoProperties) {
    Map<String, Map<String, String>> resultMap = new HashMap<>();
    for (Entry<String, String> entry : mongoProperties.entrySet()) {
      Optional<String> referenceKey = extractMongoReferenceKey(entry.getKey());
      if (referenceKey.isEmpty()) {
        continue;
      }
      String foundKey = referenceKey.get();
      resultMap.computeIfPresent(foundKey, (k, map) -> {
        Map<String, String> auxMap = new HashMap<>(map);
        auxMap.put(entry.getKey().replace(foundKey.concat("."), ""), entry.getValue());
        return Map.copyOf(auxMap);
      });
      resultMap.computeIfAbsent(foundKey, e -> Map.of(entry.getKey().replace(foundKey.concat("."), ""), entry.getValue()));
    }
    return resultMap;
  }

  private static Optional<String> extractMongoReferenceKey(String key) {
    if (!key.contains(".")) {
      return Optional.empty();
    }
    int firstDotIndex = key.indexOf(".");
    return Optional.of(key.substring(0, firstDotIndex));
  }

  private static CardQueryResponse processCardRequest(CardQueryRequest cardQueryRequest, MongoCountCardProcessor mongoCountCardProcessor,
      MongoListCardProcessor mongoListCardProcessor) {
    if (cardQueryRequest instanceof MongoCountCardRequest mongoCountCardRequest) {
      return mongoCountCardProcessor.process(mongoCountCardRequest);
    } else if (cardQueryRequest instanceof MongoListCardRequest mongoListCardRequest) {
      return mongoListCardProcessor.process(mongoListCardRequest);
    }
    throw new RuntimeException("Unknown card type");
  }

}
