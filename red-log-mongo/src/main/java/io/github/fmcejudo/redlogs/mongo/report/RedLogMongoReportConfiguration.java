package io.github.fmcejudo.redlogs.mongo.report;

import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.ExecutionService;
import io.github.fmcejudo.redlogs.report.ReportService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
class RedLogMongoReportConfiguration {

  @Bean
  @ConditionalOnMissingBean(ReportService.class)
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  ReportService reportService(@Qualifier("redlogMongoTemplate") final MongoTemplate mongoTemplate,
      final RedlogMongoProperties redLogMongoProperties) {
    return new MongoReportService(mongoTemplate, redLogMongoProperties);
  }

  @Bean
  @ConditionalOnMissingBean(ReportService.class)
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  ExecutionService executionService(@Qualifier("redlogMongoTemplate") final MongoTemplate mongoTemplate,
      final RedlogMongoProperties redLogMongoProperties) {
    return new MongoExecutionService(mongoTemplate, redLogMongoProperties);
  }

}