package io.github.fmcejudo.redlogs.report.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record Execution(String id, String application, Map<String, String> parameters, LocalDate reportDate, LocalDateTime createdAt) {

    public Execution(String id, String application, Map<String, String> parameters, LocalDate reportDate) {
        this(id, application, parameters, reportDate, LocalDateTime.now());
    }
}
