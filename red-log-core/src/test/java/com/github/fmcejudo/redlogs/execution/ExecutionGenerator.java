package com.github.fmcejudo.redlogs.execution;

import io.github.fmcejudo.redlogs.report.domain.Execution;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@FunctionalInterface
interface ExecutionGenerator {

    public Execution generate();

    public static ExecutionGenerator forAppName(final String appName) {
        return () -> {
            UUID uuid = UUID.randomUUID();
            return new Execution(uuid.toString(), appName, Map.of(), LocalDate.now());
        };
    }

    default ExecutionGenerator withParameters(Map<String, String> parameters) {
        return () -> {
            Execution execution = this.generate();
            return new Execution(execution.id(), execution.application(), parameters, execution.reportDate());
        };
    }

    public default ExecutionGenerator withReportDate(LocalDate localDate) {
        return () -> {
            Execution execution = this.generate();
            return new Execution(execution.id(), execution.application(), execution.parameters(), localDate);
        };
    }

    default ExecutionGenerator withAppName(String appName) {
        return () -> {
            Execution execution = this.generate();
            return new Execution(execution.id(), appName, execution.parameters(), execution.reportDate());
        };
    }
}
