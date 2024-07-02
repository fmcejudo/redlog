package com.github.fmcejudo.redlogs.report;

import org.joda.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Execution>> getExecutionList() {
        return ResponseEntity.ok(List.of());
    }

    record Execution(String executionId, String application, Map<String, String> parameters, LocalDate reportDate) {

    }
}

