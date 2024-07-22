package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.report.domain.Report;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${redlog.report.controller-path:report}")
public class ReportController {

    private final ReportServiceProxy reportServiceProxy;

    ReportController(final ReportServiceProxy reportServiceProxy) {
        this.reportServiceProxy = reportServiceProxy;
    }

    @GetMapping(value = "/execution/{executionId}/doc")
    public ResponseEntity<String> getAdocReport(@PathVariable String executionId) {
        return ResponseEntity.ok(reportServiceProxy.content(executionId));
    }

    @GetMapping("/execution/{executionId}/json")
    public ResponseEntity<Report> getJsonReport(@PathVariable String executionId) {
        return ResponseEntity.ok(reportServiceProxy.getJson(executionId));
    }

}

