package com.github.fmcejudo.redlogs.report.domain;

import java.util.List;

public record ReportSection(String reportId, String description, String link, List<ReportItem> items) {

}