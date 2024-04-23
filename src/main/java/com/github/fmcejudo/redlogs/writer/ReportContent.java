package com.github.fmcejudo.redlogs.writer;

import java.util.List;

public record ReportContent(String sectionId, String description, String link, List<ReportItem> items) {

    int size() {
        return items.size();
    }
}
