package com.github.fmcejudo.redlogs.execution;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.report.ExecutionService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@AutoConfiguration
class ExecutionConfiguration {

  @Bean
  @ConditionalOnClass(value = Flux.class)
  @ConditionalOnRedlogEnabled
  ReactiveExecutionController reactiveExecutionController(final ExecutionService executionService) {
    return new ReactiveExecutionController(executionService);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(ReactiveExecutionController.class)
  WebExecutionController webExecutionController(final ExecutionService executionService) {
    return new WebExecutionController(executionService);
  }
}
