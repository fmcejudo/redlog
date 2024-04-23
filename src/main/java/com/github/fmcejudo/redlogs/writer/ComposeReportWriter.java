package com.github.fmcejudo.redlogs.writer;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComposeReportWriter implements ReportWriter {

    @Override
    public void write(List<ReportContent> reportContent) {
        new ConsoleReportWriter().write(reportContent);
        new FileReportWriter().write(reportContent);
    }
}
