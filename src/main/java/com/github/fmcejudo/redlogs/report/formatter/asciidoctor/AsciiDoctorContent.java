package com.github.fmcejudo.redlogs.report.formatter.asciidoctor;

import com.github.fmcejudo.redlogs.report.domain.Report;

import java.util.List;

@FunctionalInterface
public interface AsciiDoctorContent {

    String content(final List<Report> reports);
}
