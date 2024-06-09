package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;

import java.time.LocalDate;
import java.util.List;

class ReportServiceProxy {

    private final ReportRepository reportRepository;
    private final DocumentFormat reportService;

    public ReportServiceProxy(final ReportRepository reportRepository, final DocumentFormat reportService) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    public String content(final ReportContext reportContext) {
        String applicationName = reportContext.applicationName();
        LocalDate reportDate = reportContext.reportDate();
        //List<Report> reports = reportRepository.getReportCompareWithDate(applicationName, reportDate);
        return reportService.get(applicationName, List.of());
    }
}
