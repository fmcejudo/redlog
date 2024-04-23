package com.github.fmcejudo.redlogs.writer;

import java.util.List;
import java.util.Map;

public class ConsoleReportWriter implements ReportWriter {

    public void write(final List<ReportContent> reportContent) {
       reportContent.forEach(r -> {
           System.out.printf(write(r));
       });
    }

    private String write(final ReportContent reportContent) {
        StringBuilder builder = new StringBuilder("\n\n* **")
                .append(reportContent.description()).append("**").append(": ")
                .append(reportContent.size()).append(" elements\n\n");

        reportContent.items().forEach(cre -> {
            builder.append("""
                    - %s\
                    count: %d
                    
                    """.formatted(formatLabels(cre.labels()), cre.count()));
        });
        return builder.toString();
    }

    private String formatLabels(final Map<String, String> labels) {
        StringBuilder builder = new StringBuilder();
        labels.forEach((key, value) -> {
            builder.append(key).append(" : ").append(value).append("\n").append("  ");
        });
        return builder.toString();

    }
}
