package com.github.fmcejudo.redlogs.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReportContext {

    private final String applicationName;
    private final LocalDate reportDate;
    private final Map<String, String> parameters;

    ReportContext(String applicationName, Map<String, String> parameters) {
        this.applicationName = applicationName;
        this.reportDate = Optional.ofNullable(parameters.get("date")).map(LocalDate::parse).orElse(LocalDate.now());
        Map<String, String> _parameters = new HashMap<>(parameters);
        _parameters.remove("date");
        this.parameters = _parameters;
    }

    public String applicationName() {
        return applicationName;
    }

    public LocalDate reportDate() {
        return reportDate;
    }

    public Map<String, String> parameters() {
        return parameters;
    }
}
