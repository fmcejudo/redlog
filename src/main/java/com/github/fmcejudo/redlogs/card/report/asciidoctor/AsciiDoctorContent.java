package com.github.fmcejudo.redlogs.card.report.asciidoctor;

import com.github.fmcejudo.redlogs.card.report.Report;

import java.util.List;

@FunctionalInterface
public interface AsciiDoctorContent {

    String content(final List<Report> reports);
}
