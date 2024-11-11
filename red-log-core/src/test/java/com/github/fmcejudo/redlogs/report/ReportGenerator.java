package com.github.fmcejudo.redlogs.report;

import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@FunctionalInterface
public interface ReportGenerator {

    public Report generate();

    public static ReportGenerator fromDate(LocalDate date) {
        return () -> {
            return new Report(null, date, Map.of(), List.of());
        };
    }

    public static ReportGenerator fromCurrentDate() {
        return ReportGenerator.fromDate(LocalDate.now());
    }

    public default ReportGenerator withParams(final Map<String, String> params) {
        return () -> {
            Report report = this.generate();
            return new Report(report.applicationName(), report.reportDate(), params, report.sections());
        };
    }

    public default ReportGenerator withApplicationName(final String applicationName) {
        return () -> {
            Report report = this.generate();
            return new Report(applicationName, report.reportDate(), report.params(), report.sections());
        };
    }

    public default ReportGenerator addSection(final Consumer<List<ReportSection>> reportSectionConsumer) {
     return () -> {
         Report report = this.generate();
         List<ReportSection> reportSections = new ArrayList<>(report.sections());
         reportSectionConsumer.accept(reportSections);
         return new Report(report.applicationName(), report.reportDate(), report.params(), reportSections);
     };
    }
}
