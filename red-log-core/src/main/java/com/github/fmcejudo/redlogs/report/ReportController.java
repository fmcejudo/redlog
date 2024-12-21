package com.github.fmcejudo.redlogs.report;

import io.github.fmcejudo.redlogs.report.domain.Report;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${redlog.report.controller-path:report}")
public class ReportController {

    private final ReportReaderService reportReaderService;

    ReportController(final ReportReaderService reportReaderService) {
        this.reportReaderService = reportReaderService;
    }

    @GetMapping(value = "/execution/{executionId}/doc")
    public ResponseEntity<String> getAdocReport(@PathVariable String executionId) {
        return ResponseEntity.ok(reportReaderService.asBinaryPDF(executionId));
    }

    @GetMapping("/execution/{executionId}/json")
    public ResponseEntity<Report> getJsonReport(@PathVariable String executionId) {
        return ResponseEntity.ok(reportReaderService.asReport(executionId));
    }

}

