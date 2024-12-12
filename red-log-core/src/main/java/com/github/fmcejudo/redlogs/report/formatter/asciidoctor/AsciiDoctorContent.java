package com.github.fmcejudo.redlogs.report.formatter.asciidoctor;

import io.github.fmcejudo.redlogs.report.domain.Report;

@FunctionalInterface
public interface AsciiDoctorContent {

    String content(final Report report);
}
