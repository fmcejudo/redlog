package io.github.fmcejudo.redlogs.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface MongoCardQueryProcessor extends CardQueryProcessor {

  public static CardQueryProcessor createProcessor(final Map<String, String> connectionDetails) {

    List<MongoConnectionProperties> mongoConnectionsDetails = parse(connectionDetails);

    MongoDBConfig mongoDBConfig = new MongoDBConfig(mongoConnectionsDetails.getFirst());
    MongoTemplate mongoTemplate = mongoDBConfig.mongoTemplate();
    MongoCountCardProcessor mongoCountCardProcessor = new MongoCountCardProcessor(mongoTemplate);
    MongoListCardProcessor mongoListCardProcessor = new MongoListCardProcessor(mongoTemplate);
    return cardQueryRequest -> {
      try {
        return processCardRequest(cardQueryRequest, mongoCountCardProcessor, mongoListCardProcessor);
      } catch (Exception e) {
        return CardQueryResponse.from(cardQueryRequest).failure(e.getMessage());
      }
    };
  }

  private static List<MongoConnectionProperties> parse(Map<String, String> mongoProperties) {
    Map<String, List<Entry<String, String>>> groupingByReference =
        mongoProperties.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().split("\\.")[0]));

    List<MongoConnectionProperties> mongoConnectionProperties = new ArrayList<>();
    for (List<Entry<String, String>> entries : groupingByReference.values()) {
      Map<String, String> details =
          entries.stream().collect(Collectors.toMap(e -> e.getKey().replaceFirst(".*\\.",""), Entry::getValue));
      mongoConnectionProperties.add(MongoConnectionProperties.from(details));
    }
    return List.copyOf(mongoConnectionProperties);
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
