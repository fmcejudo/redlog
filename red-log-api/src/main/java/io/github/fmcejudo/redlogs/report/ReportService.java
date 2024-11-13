package io.github.fmcejudo.redlogs.report;

import io.github.fmcejudo.redlogs.report.domain.Report;

public interface ReportService {

  Report findReport(String executionId);
}

