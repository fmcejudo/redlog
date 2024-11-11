package com.github.fmcejudo.redlogs.execution;

import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
final class MongoExecutionService implements ExecutionService {

  private final MongoTemplate mongoTemplate;

  private final String collectionName;

  public MongoExecutionService(MongoTemplate mongoTemplate, RedLogMongoProperties redLogMongoProperties) {
    this.mongoTemplate = mongoTemplate;
    this.collectionName = MongoNamingUtils.composeCollectionName(
        redLogMongoProperties.getCollectionNamePrefix(),
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
