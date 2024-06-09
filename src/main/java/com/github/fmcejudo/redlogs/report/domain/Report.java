package com.github.fmcejudo.redlogs.report.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record Report(String applicationName, LocalDate reportDate,
                     Map<String,String> params, List<ReportSection> sections) {
}


