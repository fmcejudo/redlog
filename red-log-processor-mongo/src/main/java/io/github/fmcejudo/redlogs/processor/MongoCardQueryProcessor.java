package io.github.fmcejudo.redlogs.processor;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface MongoCardQueryProcessor extends CardQueryProcessor {

  public static CardQueryProcessor createProcessor(final Map<String, String> connectionDetails) {
    MongoDBConfig mongoDBConfig = new MongoDBConfig(MongoConnectionProperties.from(connectionDetails));
    MongoTemplate mongoTemplate = mongoDBConfig.mongoTemplate();
    MongoCountCardProcessor mongoCountCardProcessor = new MongoCountCardProcessor(mongoTemplate);
    MongoListCardProcessor mongoListCardProcessor = new MongoListCardProcessor(mongoTemplate);
    return cardQueryRequest -> {
      if (cardQueryRequest instanceof MongoCountCardRequest mongoCountCardRequest) {
        return mongoCountCardProcessor.process(mongoCountCardRequest);
      } else if (cardQueryRequest instanceof MongoListCardRequest mongoListCardRequest) {
        return mongoListCardProcessor.process(mongoListCardRequest);
      }
      throw new RuntimeException("Unknown card type");
    };
  }
}
