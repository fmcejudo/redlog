package com.github.fmcejudo.redlogs.card.writer;

import java.time.LocalDate;
import java.util.Map;

import com.github.fmcejudo.redlogs.execution.domain.Execution;

@FunctionalInterface
interface ExecutionBuilder {

  public Execution build();

  public static ExecutionBuilder withExecutionIdAndApplication(final String executionId, final String application) {
    return () -> new Execution(executionId, application, Map.of(), LocalDate.now());
  }

  default ExecutionBuilder withParameters(final Map<String, String> parameters) {
    return () -> {
      Execution e = this.build();
      return new Execution(e.id(), e.application(), parameters, e.reportDate());
    };
  }

  default ExecutionBuilder withReportDate(final LocalDate reportDate) {
    return () -> {
      Execution e = this.build();
      return new Execution(e.id(), e.application(), e.parameters(), reportDate);
    };
  }
}
