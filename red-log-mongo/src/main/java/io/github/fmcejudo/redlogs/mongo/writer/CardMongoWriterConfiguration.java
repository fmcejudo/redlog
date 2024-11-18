package io.github.fmcejudo.redlogs.mongo.writer;

import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
class CardMongoWriterConfiguration {

  @Bean
  @ConditionalOnBean(value = MongoTemplate.class)
  CardExecutionWriter cardExecutionWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate redlogMongoTemplate) {
    return new CardExecutionMongoWriter(redlogMongoTemplate);
  }

  @Bean
  @ConditionalOnBean(value = MongoTemplate.class)
  CardReportWriter cardReportWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate redlogMongoTemplate) {
    return new CardReportMongoWriter(redlogMongoTemplate);
  }

}
