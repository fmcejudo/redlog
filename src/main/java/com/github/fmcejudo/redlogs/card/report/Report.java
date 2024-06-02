package com.github.fmcejudo.redlogs.card.report;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

public record Report(String reportId,
              @Field("date") LocalDate lastUpdated,
              String link,
              String description,
              @Field("items") List<ReportItem> items,
              @Field("previousItem") List<ReportItem> previousItems) {
}

