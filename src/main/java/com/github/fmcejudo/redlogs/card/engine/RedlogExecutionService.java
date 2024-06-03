package com.github.fmcejudo.redlogs.card.engine;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;
import java.util.Map;

public interface RedlogExecutionService {

    void saveExecution(String executionId, CardContext cardContext);


    void updateExecution(String executionId, String status);
}

@Configuration
class RedlogExecutionConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedlogExecutionService.class)
    RedlogExecutionService redlogExecutionService(final MongoTemplate mongoTemplate,
                                                  final RedLogMongoProperties redLogMongoProperties) {
        return new DefaultRedlogExecutionService(mongoTemplate, redLogMongoProperties);
    }
}

class DefaultRedlogExecutionService implements RedlogExecutionService {

    private final MongoTemplate mongoTemplate;
    private final String mongoPrefix;

    public DefaultRedlogExecutionService(MongoTemplate mongoTemplate, RedLogMongoProperties redLogMongoProperties) {
        this.mongoTemplate = mongoTemplate;
        this.mongoPrefix = redLogMongoProperties.getCollectionNamePrefix();
    }

    @Override
    public void saveExecution(String executionId, CardContext cardContext) {
        RedlogExecution redlogExecution = new RedlogExecution(
                executionId, cardContext.applicationName(), cardContext.parameters(),
                cardContext.reportDate(), "PROCESSING"
        );
        mongoTemplate.save(redlogExecution, MongoNamingUtils.composeCollectionName(mongoPrefix, "executions"));
    }

    @Override
    public void updateExecution(String executionId, String status) {

    }

    record RedlogExecution(@Id String executionId, String applicationName, Map<String, String> parameters,
                           LocalDate reportDate, String status) {

    }


}
