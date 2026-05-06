package io.github.fmcejudo.redlogs.processor;

import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;

final class MongoTemplateFactory {

  private final Map<String, MongoTemplate> mongoTemplateMap;

  private MongoTemplateFactory(Map<String, MongoTemplate> mongoTemplateMap) {
    this.mongoTemplateMap = mongoTemplateMap;
  }

  static MongoTemplateFactory init(Map<String, MongoTemplate> mongoTemplateMap) {
    if (mongoTemplateMap == null || mongoTemplateMap.isEmpty()) {
      throw new IllegalStateException("at least requires a mongo template");
    }
    return new MongoTemplateFactory(Map.copyOf(mongoTemplateMap));
  }

  MongoTemplate find(String mongoTemplateId) {
    if (mongoTemplateId == null || mongoTemplateId.isBlank()) {
      throw new IllegalArgumentException("mongo template id is mandatory and not to be blank");
    }
    MongoTemplate mongoTemplate = mongoTemplateMap.get(mongoTemplateId);
    if (mongoTemplate == null) {
      throw new IllegalStateException("it didn't find a mongo template for specified id: %s".formatted(mongoTemplateId));
    }
    return mongoTemplate;
  }
}
