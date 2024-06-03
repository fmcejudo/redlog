package com.github.fmcejudo.redlogs.card.report;

import com.github.fmcejudo.redlogs.card.CardContext;

import java.time.LocalDate;
import java.util.List;

class ReportServiceProxy {

    private final ReportRepository reportRepository;
    private final ReportService reportService;

    public ReportServiceProxy(final ReportRepository reportRepository, final ReportService reportService) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    public String content(final CardContext cardContext) {
        String applicationName = cardContext.applicationName();
        LocalDate reportDate = cardContext.reportDate();
        List<Report> reports = reportRepository.getReportCompareWithDate(applicationName, reportDate);
        return reportService.get(applicationName, reports);
    }
}
