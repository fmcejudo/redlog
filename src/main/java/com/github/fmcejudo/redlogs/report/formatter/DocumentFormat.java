package com.github.fmcejudo.redlogs.report.formatter;

import com.github.fmcejudo.redlogs.report.domain.Report;

import java.util.List;

public interface DocumentFormat {

    String get(List<Report> reports);

}
