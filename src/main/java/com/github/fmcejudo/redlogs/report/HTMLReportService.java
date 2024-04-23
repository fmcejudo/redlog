package com.github.fmcejudo.redlogs.report;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;

@Component
@Qualifier("htmlReportService")
class HTMLReportService implements ReportService<String> {

    private final ReportRepository reportRepository;

    private final SpringTemplateEngine springTemplateEngine;

    public HTMLReportService(final ReportRepository reportRepository, final SpringTemplateEngine springTemplateEngine) {
        this.reportRepository = reportRepository;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Override
    public String get(String applicationName) {
        List<Report> reports = reportRepository.findByApplicationName(applicationName);
        Context context = new Context();
        context.setVariables(Map.of("reports", reports, "name", applicationName));
        return springTemplateEngine.process("report.html", context);
    }

}
