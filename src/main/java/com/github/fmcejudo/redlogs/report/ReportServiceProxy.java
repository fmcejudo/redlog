package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;

import java.util.List;

class ReportServiceProxy {

    private final ReportService reportService;
    private final DocumentFormat documentFormat;

    ReportServiceProxy(final ReportService reportService, final DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
        this.reportService = reportService;
    }

    String content(final ReportContext reportContext) {
        List<Report> reports = reportService.findReports(reportContext);
        return documentFormat.get(reports);
    }
}
