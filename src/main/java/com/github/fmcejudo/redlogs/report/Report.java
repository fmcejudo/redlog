package com.github.fmcejudo.redlogs.report;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

record Report(String reportId,
              @Field("date") LocalDate lastUpdated,
              String link,
              String description,
              @Field("items") List<ReportItem> items,
              @Field("previousItem") List<ReportItem> previousItems) {
}

record ReportItem(Map<String, String> labels, long count){

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
