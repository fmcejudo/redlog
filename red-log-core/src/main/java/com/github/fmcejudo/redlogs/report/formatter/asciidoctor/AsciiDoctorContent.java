package com.github.fmcejudo.redlogs.report.formatter.asciidoctor;

import com.github.fmcejudo.redlogs.report.domain.Report;

@FunctionalInterface
public interface AsciiDoctorContent {

    String content(final Report report);
}
