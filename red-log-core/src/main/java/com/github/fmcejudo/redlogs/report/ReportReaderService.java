package com.github.fmcejudo.redlogs.report;

import io.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;

class ReportReaderService {

    private final ReportService reportService;
    private final DocumentFormat documentFormat;

    ReportReaderService(final ReportService reportService,
                        final DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
        this.reportService = reportService;
    }

    String asBinaryPDF(final String executionId) {
        Report report = reportService.findReport(executionId);
        return documentFormat.get(report);
    }

    public Report asReport(String executionId) {
        return reportService.findReport(executionId);
    }
}
