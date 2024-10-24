package com.github.fmcejudo.redlogs.execution;

import com.github.fmcejudo.redlogs.card.writer.CardExecutionAppender;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@AutoConfiguration(after = MongoTemplate.class)
class ExecutionConfiguration {

    @Bean
    ExecutionController executionController(ExecutionService executionService) {
        return new ExecutionController(executionService);
    }

    @Bean
    @ConditionalOnMissingBean(CardExecutionAppender.class)
    CardExecutionAppender cardExecutionAppender(final MongoTemplate mongoTemplate,
                                                final RedLogMongoProperties redLogMongoProperties) {
        return new MongoExecutionService(mongoTemplate, redLogMongoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ExecutionService.class)
    ExecutionService executionService(final MongoTemplate mongoTemplate,
                                      final RedLogMongoProperties redLogMongoProperties) {
        return new MongoExecutionService(mongoTemplate, redLogMongoProperties);
    }
}
