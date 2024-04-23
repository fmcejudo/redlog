package com.github.fmcejudo.redlogs.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

class FileReportWriter implements ReportWriter {

    private static final String URL_ALERTHUB =
            "https://sscc.central.inditex.grp/alerthui/web/alerthub/alert-definitions/summary/pro/";

    @Override
    public void write(List<ReportContent> reportContent) {
        String fileName = "shift.adoc";
        if (Files.exists(Paths.get(fileName))) {
            Paths.get(fileName).toFile().delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (ReportContent content : reportContent) {
                writer.write(this.write(content));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private String write(final ReportContent reportContent) {
        StringBuilder builder = new StringBuilder("\n\n* **")
                .append(reportContent.description()).append("**").append(": ")
                .append(reportContent.size()).append(" elements ").append(" - link: \n")
                .append("link:").append(reportContent.link())
                .append("[")
                .append(reportContent.sectionId())
                .append("]").append("\n\n");

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
        labels.forEach((key, value) -> builder.append(keyValuePair(key, value)).append(" +\n").append("  "));
        return builder.toString();
    }

    private String keyValuePair(String key, String value) {
        if (key.equals("alert")) {
            return "*alert* : \nlink:%s[%s]".formatted(URL_ALERTHUB + value, value);
        } else {
            return "*" + key + "* : " + value;
        }
    }
}
