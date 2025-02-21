package io.github.fmcejudo.redlogs.processor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

class MongoListCardProcessor implements MongoCardQueryProcessor {

  private final MongoTemplate mongoTemplate;

  MongoListCardProcessor(final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public CardQueryResponse process(CardQueryRequest cardQueryRequest) {
    Assert.isInstanceOf(MongoListCardRequest.class, cardQueryRequest);
    MongoListCardRequest mlr = (MongoListCardRequest) cardQueryRequest;
    List<CardQueryResponseEntry> entries = retrieveList(mlr);
    return CardQueryResponse.success(
        LocalDate.now(), mlr.id(), mlr.executionId(), mlr.description(), null, mlr.tags(), entries
    );
  }

  private List<CardQueryResponseEntry> retrieveList(final MongoListCardRequest mlcr) {
    Query query = new BasicQuery(mlcr.query());
    query.fields().include(mlcr.fields());
    List<Document> documents = mongoTemplate.find(query, Document.class, mlcr.collection());
    List<CardQueryResponseEntry> result = new ArrayList<>();
    for (Document document : documents) {
      Map<String, String> labels =
          document.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> String.valueOf(e.getValue())));
      result.add(new CardQueryResponseEntry(labels, -1));
    }
    return result;
  }
}
