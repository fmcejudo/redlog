package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;

class ReportServiceProxy {

    private final ReportService reportService;
    private final DocumentFormat documentFormat;

    ReportServiceProxy(final ReportService reportService,
                       final DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
        this.reportService = reportService;
    }

    String content(final String executionId) {
        Report report = reportService.findReport(executionId);
        return documentFormat.get(report);
    }

    public Report getJson(String executionId) {
        return reportService.findReport(executionId);
    }
}
