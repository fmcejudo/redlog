package io.github.fmcejudo.redlogs.report.domain;

import java.time.LocalDate;
import java.util.Map;

@FunctionalInterface
public interface ExecutionBuilder {

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
