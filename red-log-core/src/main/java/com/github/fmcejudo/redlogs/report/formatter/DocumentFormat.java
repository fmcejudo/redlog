package com.github.fmcejudo.redlogs.report.formatter;

import com.github.fmcejudo.redlogs.report.domain.Report;

public interface DocumentFormat {

    String get(Report reports);

}
