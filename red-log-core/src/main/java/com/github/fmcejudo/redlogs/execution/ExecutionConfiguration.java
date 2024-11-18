package com.github.fmcejudo.redlogs.execution;

import io.github.fmcejudo.redlogs.report.ExecutionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
class ExecutionConfiguration {

  @Bean
  @ConditionalOnBean(ExecutionService.class)
  ExecutionController executionController(final ExecutionService executionService) {
    return new ExecutionController(executionService);
  }
}
