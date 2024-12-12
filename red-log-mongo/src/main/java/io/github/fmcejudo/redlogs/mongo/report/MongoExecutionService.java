package io.github.fmcejudo.redlogs.mongo.report;

import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.mongo.MongoNamingUtils;
import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.ExecutionService;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

final class MongoExecutionService implements ExecutionService {

  private final MongoTemplate mongoTemplate;

  private final String collectionName;

  public MongoExecutionService(MongoTemplate mongoTemplate, RedlogMongoProperties redlogMongoProperties) {
    this.mongoTemplate = mongoTemplate;
    this.collectionName = MongoNamingUtils.composeCollectionName(
        redlogMongoProperties.getCollectionNamePrefix(),
        "executions"
    );
  }

  @Override
  public List<Execution> findExecutionWithParameters(String appName, Map<String, String> parameters) {
    Criteria criteria = Criteria.where("application").is(appName);
    for (Map.Entry<String, String> parameter : parameters.entrySet()) {
      criteria = criteria.and("parameters." + parameter.getKey()).is(parameter.getValue());
    }
    Query query = Query.query(criteria);
    return mongoTemplate.find(query, Execution.class, collectionName);
  }
}
