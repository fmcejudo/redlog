package io.github.fmcejudo.redlogs.mongo.writer;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
public class CardMongoWriterConfiguration {

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  CardExecutionWriter cardExecutionWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate redlogMongoTemplate) {
    return new CardExecutionMongoWriter(redlogMongoTemplate);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  CardReportWriter cardReportWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate redlogMongoTemplate) {
    return new CardReportMongoWriter(redlogMongoTemplate);
  }

}
