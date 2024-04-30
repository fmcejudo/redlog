package com.github.fmcejudo.redlogs.engine.card.writer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
class CardResponseWriterConfig {

    @Bean
    @ConditionalOnMissingBean(CardResponseWriter.class)
    CardResponseWriter cardResponseWriter(MongoTemplate mongoTemplate) {
        return new CardResponseMongoWriter(mongoTemplate);
    }
}
