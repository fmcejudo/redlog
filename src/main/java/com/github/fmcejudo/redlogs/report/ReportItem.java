package com.github.fmcejudo.redlogs.report;

import java.util.Map;
import java.util.Objects;

public record ReportItem(Map<String, String> labels, long count) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportItem that = (ReportItem) o;
        return Objects.equals(labels, that.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(labels);
    }
}
