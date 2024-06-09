package com.github.fmcejudo.redlogs.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/${redlog.report.controller-path:report}")
class ReportController {

    private final ReportServiceProxy reportServiceProxy;

    public ReportController(final ReportServiceProxy reportServiceProxy) {
        this.reportServiceProxy = reportServiceProxy;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> getAdocReport(@PathVariable String applicationName,
                                                @RequestParam(required = false) final Map<String, String> parameters) {
        ReportContext reportContext = new ReportContext(applicationName, parameters);
        return ResponseEntity.ok(reportServiceProxy.content(reportContext));
    }
}

class ReportContext {

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
