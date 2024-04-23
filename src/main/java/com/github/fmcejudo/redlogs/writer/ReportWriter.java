package com.github.fmcejudo.redlogs.writer;

import java.util.List;

public interface ReportWriter {

    void write(List<ReportContent> reportContent);
}
