package com.github.fmcejudo.redlogs.card.engine.writer;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
class CardResponseWriterConfig {

    @Bean
    @ConditionalOnMissingBean({CardResponseWriter.class})
    CardResponseWriter cardResponseWriter(MongoTemplate mongoTemplate,
                                          RedLogMongoProperties redLogMongoProperties) {
        return new CardResponseMongoWriter(mongoTemplate, redLogMongoProperties);
    }
}
