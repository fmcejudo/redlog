package com.github.fmcejudo.redlogs.report;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "card_report")
record Report(String id,
              String applicationName,
              @Field("dateTime") LocalDateTime lastUpdated,
              String link,
              String description,
              @Field("currentEntries") List<ReportItem> items,
              @Field("previousEntries") List<ReportItem> previousItems) {
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
