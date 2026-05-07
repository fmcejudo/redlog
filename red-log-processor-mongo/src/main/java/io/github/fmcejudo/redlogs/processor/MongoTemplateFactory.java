package io.github.fmcejudo.redlogs.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import io.github.fmcejudo.redlogs.type.MongoConfig;
import org.springframework.data.mongodb.core.MongoTemplate;

final class MongoTemplateFactory {

  private final Map<String, MongoTemplate> mongoTemplateMap;

  private MongoTemplateFactory(Map<String, MongoTemplate> mongoTemplateMap) {
    this.mongoTemplateMap = mongoTemplateMap;
  }

  static MongoTemplateFactory init(Map<String, MongoConnectionProperties> mongoConnectionPropertiesMap) {
    if (mongoConnectionPropertiesMap == null || mongoConnectionPropertiesMap.isEmpty()) {
      throw new IllegalStateException("at least requires a mongo configuration properties to connect to");
    }
    Map<String, MongoTemplate> auxMap = new HashMap<>();
    for (Entry<String, MongoConnectionProperties> entry : mongoConnectionPropertiesMap.entrySet()) {
      auxMap.put(entry.getKey(), new MongoDBConfig(entry.getValue()).mongoTemplate());
    }
    return new MongoTemplateFactory(Map.copyOf(auxMap));
  }

  Optional<MongoTemplate> find(String mongoTemplateId) {
    if (mongoTemplateId == null || mongoTemplateId.isBlank()) {
      throw new IllegalArgumentException("mongo template id is mandatory and not to be blank");
    }
    MongoTemplate mongoTemplate = mongoTemplateMap.get(mongoTemplateId);
    return Optional.ofNullable(mongoTemplate);
  }

}
