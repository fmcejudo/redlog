package io.github.fmcejudo.redlogs.mongo.writer;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
public class CardMongoWriterConfiguration {

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(value = MongoTemplate.class)
  CardExecutionWriter cardExecutionWriter(final MongoTemplate redlogMongoTemplate) {
    return new CardExecutionMongoWriter(redlogMongoTemplate);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(value = MongoTemplate.class)
  CardReportWriter cardReportWriter(final MongoTemplate redlogMongoTemplate) {
    return new CardReportMongoWriter(redlogMongoTemplate);
  }

}
