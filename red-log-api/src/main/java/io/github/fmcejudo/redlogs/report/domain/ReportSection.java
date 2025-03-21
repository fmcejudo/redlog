package io.github.fmcejudo.redlogs.report.domain;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.List;

public record ReportSection(String id, String executionId, String reportId, String description, String link,
                            List<ReportItem> items, LocalDateTime createdAt, List<String> tags) {

    public ReportSection(String executionId, String reportId, String description, String link, List<ReportItem> items, List<String> tags) {
        this(String.join(".", reportId, executionId), executionId, reportId, description, link, items, now(), tags);
    }

    public ReportSection(String executionId, String reportId, String description, String link,
                         List<ReportItem> items, LocalDateTime createdAt, List<String> tags) {
        this(String.join(".", reportId, executionId), executionId, reportId, description, link, items, createdAt, tags);
    }
}