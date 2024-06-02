package com.github.fmcejudo.redlogs.card.report;

import java.util.List;

public interface ReportService {

    String get(String applicationName, List<Report> reports);

}
