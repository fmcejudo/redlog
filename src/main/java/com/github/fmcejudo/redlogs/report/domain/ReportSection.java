package com.github.fmcejudo.redlogs.report.domain;

import java.util.List;

public record ReportSection(String id, String executionId, String reportId, String description, String link,
                            List<ReportItem> items) {

    public ReportSection(String executionId, String reportId, String description, String link, List<ReportItem> items) {
        this(String.join(".", reportId, executionId), executionId, reportId, description, link, items);
    }
}