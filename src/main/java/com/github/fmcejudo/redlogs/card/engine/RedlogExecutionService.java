package com.github.fmcejudo.redlogs.card.engine;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RedlogExecutionService {

    void saveOrReplaceExecution(String executionId, CardContext cardContext);

    void updateExecution(String executionId, String status);

    String findExecutionId(CardContext cardContext);
}

class DefaultRedlogExecutionService implements RedlogExecutionService {

    private final MongoTemplate mongoTemplate;
    private final String mongoPrefix;
    private final String executionsCollectionName;

    public DefaultRedlogExecutionService(MongoTemplate mongoTemplate, RedLogMongoProperties redLogMongoProperties) {
        this.mongoTemplate = mongoTemplate;
        this.mongoPrefix = redLogMongoProperties.getCollectionNamePrefix();
        this.executionsCollectionName = MongoNamingUtils.composeCollectionName(mongoPrefix, "executions");
    }

    @Override
    public void saveOrReplaceExecution(String executionId, CardContext cardContext) {
        removePreviousDataAndExecutions(cardContext);

        RedlogExecution redlogExecution = new RedlogExecution(
                executionId, cardContext.applicationName(), cardContext.parameters(),
                cardContext.reportDate(), "PROCESSING"
        );
        mongoTemplate.save(redlogExecution, executionsCollectionName);
    }

    private void removePreviousDataAndExecutions(final CardContext cardContext) {
        Query query = createQueryFromCardContext(cardContext);
        List<String> executionsIdsToRemove = mongoTemplate.find(query, RedlogExecution.class, executionsCollectionName)
                .stream()
                .map(RedlogExecution::executionId)
                .toList();

        String collectionName = MongoNamingUtils.composeCollectionName(mongoPrefix, cardContext.applicationName());
        mongoTemplate.remove(Query.query(Criteria.where("executionId").in(executionsIdsToRemove)), collectionName);

        mongoTemplate.remove(query, executionsCollectionName);
    }

    @Override
    public void updateExecution(String executionId, String status) {

        RedlogExecution execution = mongoTemplate.findById(
                executionId, RedlogExecution.class, executionsCollectionName
        );
        if (execution == null) {
            throw new RuntimeException("execution id has not been found");
        }
        RedlogExecution toUpdate = new RedlogExecution(
                executionId, execution.applicationName(), execution.parameters(), execution.reportDate(), status
        );
        mongoTemplate.save(toUpdate, executionsCollectionName);
    }

    @Override
    public String findExecutionId(CardContext cardContext) {
        Query query = createQueryFromCardContext(cardContext);
        List<RedlogExecution> redlogExecutions =
                mongoTemplate.find(query, RedlogExecution.class, executionsCollectionName);
        if (redlogExecutions.isEmpty()) {
            throw new CardExecutionException("There is no execution created for provided context");
        }
        if (redlogExecutions.size() > 1) {
            throw new CardExecutionException("There are multiple execution for same context and it is inconsistent");
        }
        return redlogExecutions.getFirst().executionId();
    }

    private Query createQueryFromCardContext(final CardContext cardContext) {
        return Query.query(Criteria.where("applicationName").is(cardContext.applicationName())
                .and("parameters").is(cardContext.parameters())
                .and("reportDate").is(cardContext.reportDate()));
    }

    record RedlogExecution(@Id String executionId, String applicationName, Map<String, String> parameters,
                           LocalDate reportDate, String status) {

    }

}

class RedlogExecutionConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedlogExecutionService.class)
    RedlogExecutionService redlogExecutionService(final MongoTemplate mongoTemplate,
                                                  final RedLogMongoProperties redLogMongoProperties) {
        return new DefaultRedlogExecutionService(mongoTemplate, redLogMongoProperties);
    }
}
