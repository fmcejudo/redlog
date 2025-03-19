package io.github.fmcejudo.redlogs.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import org.bson.Document;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.util.Assert;

class MongoCountCardProcessor implements CardQueryProcessor {

  private final MongoTemplate mongoTemplate;

  public MongoCountCardProcessor(final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public CardQueryResponse process(CardQueryRequest request) {
    Assert.isInstanceOf(MongoCountCardRequest.class, request);
    MongoCountCardRequest mccr = (MongoCountCardRequest) request;
    List<CardQueryResponseEntry> entries = Function.<MongoCountCardRequest>identity()
        .andThen(this::retrieveRecordsFromDB)
        .apply(mccr);
    return CardQueryResponse.from(mccr).success(null, entries);
  }

  private List<CardQueryResponseEntry> retrieveRecordsFromDB(MongoCountCardRequest mccr) {

    Aggregation aggregation = Aggregation.newAggregation(
        createMatchOperation(mccr.query()),
        Aggregation.group(mccr.fields()).count().as("count"),
        createProjectAggregation(mccr.fields())
    );

    AggregationResults<Document> aggregate = mongoTemplate.aggregate(aggregation, mccr.collection(), Document.class);

    List<CardQueryResponseEntry> entries = new ArrayList<>();
    aggregate.iterator().forEachRemaining(d -> {
      Map<String, String> labels = d.entrySet().stream()
          .filter(e -> !"count".equals(e.getKey()))
          .collect(Collectors.toMap(Entry::getKey, e -> String.valueOf(e.getValue())));
      int count = (int) d.get("count");
      CardQueryResponseEntry cardQueryResponseEntry = new CardQueryResponseEntry(labels, count);
      entries.add(cardQueryResponseEntry);
    });
    return entries;
  }

  private MatchOperation createMatchOperation(String jsonQuery) {
    BasicQuery query = new BasicQuery(jsonQuery);
    Document filterDocument = query.getQueryObject();
    MongoExpression mongoExpression = () -> filterDocument;

    return Aggregation.match(AggregationExpression.from(mongoExpression));
  }

  private ProjectionOperation createProjectAggregation(String... fields) {
    if (fields.length == 0) {
      throw new IllegalStateException("it needs to have some labels to have details");
    }
    if (fields.length == 1) {
      return Aggregation.project().andExclude("_id").and("_id").as(fields[0]).andInclude("count");
    }
    return Aggregation.project(fields).andInclude("count").andInclude(fields).andExclude("_id");
  }
}
