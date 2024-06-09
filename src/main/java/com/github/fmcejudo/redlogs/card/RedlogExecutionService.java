package com.github.fmcejudo.redlogs.card;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public interface RedlogExecutionService {

    String saveExecution(CardContext cardContext);

    void updateExecution(CardContext cardContext, String status);
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
    public String saveExecution(CardContext cardContext) {

        String executionId = createExecutionId(cardContext);
        removePreviousData(executionId);
        RedlogExecution redlogExecution = new RedlogExecution(
                executionId, cardContext.applicationName(), cardContext.parameters(),
                cardContext.reportDate(), "PROCESSING"
        );
        mongoTemplate.save(redlogExecution, executionsCollectionName);
        return executionId;
    }

    private void removePreviousData(String executionId) {
        String reportCollectionName = MongoNamingUtils.composeCollectionName(mongoPrefix, "reports");
        mongoTemplate.remove(Query.query(Criteria.where("executionId").is(executionId)), reportCollectionName);
    }

    private String createExecutionId(final CardContext cardContext) {
        return String.join(
                ".",
                cardContext.applicationName(),
                cardContext.reportDate().format(ISO_LOCAL_DATE),
                String.valueOf(cardContext.parameters().hashCode())
        ).toLowerCase();
    }

    @Override
    public void updateExecution(CardContext cardContext, String status) {
        String executionId = createExecutionId(cardContext);

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
