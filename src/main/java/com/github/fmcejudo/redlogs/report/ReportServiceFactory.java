package com.github.fmcejudo.redlogs.report;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ReportServiceFactory {

    private final ReportService<String> htmlReportService;
    private final ReportService<String> asciidoctorReportService;

    public ReportServiceFactory(@Qualifier("asciidoctorReportService") ReportService<String> asciidoctorReportService,
                                @Qualifier("htmlReportService") ReportService<String> htmlReportService) {
        this.asciidoctorReportService = asciidoctorReportService;
        this.htmlReportService = htmlReportService;
    }

    public <T> ReportService<T> getService(final String type) {
        return switch (type) {
            case "HTML" -> (ReportService<T>) htmlReportService;
            case "ADOC" -> (ReportService<T>) asciidoctorReportService;
            default -> throw new RuntimeException("Unknown service");
        };
    }
}
