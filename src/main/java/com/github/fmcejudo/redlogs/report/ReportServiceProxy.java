package com.github.fmcejudo.redlogs.report;

import java.time.LocalDate;
import java.util.List;

class ReportServiceProxy {

    private final ReportRepository reportRepository;
    private final ReportService reportService;

    public ReportServiceProxy(final ReportRepository reportRepository, final ReportService reportService) {
        this.reportRepository = reportRepository;
        this.reportService = reportService;
    }

    public String content(String applicationName) {
        List<Report> reports = reportRepository.getReportCompareWithDate(applicationName, LocalDate.now());
        return reportService.get(applicationName, reports);
    }
}
