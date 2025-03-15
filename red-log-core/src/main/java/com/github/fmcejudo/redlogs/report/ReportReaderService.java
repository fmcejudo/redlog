package com.github.fmcejudo.redlogs.report;

import io.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import io.github.fmcejudo.redlogs.report.ReportService;
import io.github.fmcejudo.redlogs.report.domain.Report;

public interface ReportReaderService {

    String asBinaryPDF(String executionId);

    Report asReport(String executionId);
}


class DefaultReportReaderService implements ReportReaderService {

    private final ReportService reportService;
    private final DocumentFormat documentFormat;

    DefaultReportReaderService(final ReportService reportService,
                        final DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
        this.reportService = reportService;
    }

    @Override
    public String asBinaryPDF(final String executionId) {
        Report report = reportService.findReport(executionId);
        return documentFormat.get(report);
    }

    @Override
    public Report asReport(String executionId) {
        return reportService.findReport(executionId);
    }
}
