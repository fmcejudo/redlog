package com.github.fmcejudo.redlogs.execution;

import io.github.fmcejudo.redlogs.report.ExecutionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
class ExecutionConfiguration {

  @Bean
  ExecutionController executionController(final ExecutionService executionService) {
    return new ExecutionController(executionService);
  }
}
