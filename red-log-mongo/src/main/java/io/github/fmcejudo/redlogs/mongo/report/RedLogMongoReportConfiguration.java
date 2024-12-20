package io.github.fmcejudo.redlogs.mongo.report;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.ExecutionService;
import io.github.fmcejudo.redlogs.report.ReportService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
public class RedLogMongoReportConfiguration {

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(ReportService.class)
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  ReportService reportService(@Qualifier("redlogMongoTemplate") final MongoTemplate mongoTemplate, RedlogMongoProperties redlogMongoProperties) {
    return new MongoReportService(mongoTemplate, redlogMongoProperties);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(ExecutionService.class)
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  ExecutionService executionService(@Qualifier("redlogMongoTemplate") final MongoTemplate mongoTemplate, RedlogMongoProperties redlogMongoProperties) {
    return new MongoExecutionService(mongoTemplate, redlogMongoProperties);
  }

}