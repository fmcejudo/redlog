package io.github.fmcejudo.redlogs.report.formatter;

import io.github.fmcejudo.redlogs.report.domain.Report;

public interface DocumentFormat {

    String get(Report reports);

    String format();

}
