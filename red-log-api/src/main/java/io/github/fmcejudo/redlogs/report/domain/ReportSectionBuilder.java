package io.github.fmcejudo.redlogs.report.domain;

import static java.time.LocalDateTime.now;

import java.util.List;

import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;

@FunctionalInterface
public interface ReportSectionBuilder {

  public ReportSection build();

  static ReportSectionBuilder fromExecutionIdAndReportId(final String executionId, final String reportId) {
    String id = String.join(".", reportId, executionId);
    return () -> new ReportSection(id, executionId, reportId, null, null, List.of(), now());
  }

  default ReportSectionBuilder withDescription(final String description) {
    return () -> {
      ReportSection r = this.build();
      return new ReportSection(r.id(), r.executionId(), r.reportId(), description, r.link(), r.items(), now());
    };
  }

  default ReportSectionBuilder withLink(final String link) {
    return () -> {
      ReportSection r = this.build();
      return new ReportSection(r.id(), r.executionId(), r.reportId(), r.description(), link, r.items(), now());
    };
  }

  default ReportSectionBuilder withItems(final List<CardQueryResponseEntry> items) {
    return () -> {
      ReportSection r = this.build();
      var reportItems = items.stream().map(cqr -> new ReportItem(cqr.labels(), cqr.count())).toList();
      return new ReportSection(r.id(), r.executionId(), r.reportId(), r.description(), r.link(), reportItems, now());
    };
  }
}
