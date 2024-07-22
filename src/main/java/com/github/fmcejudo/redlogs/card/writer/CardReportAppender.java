package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.report.domain.ReportSection;

public interface CardReportAppender {

    public abstract void add(ReportSection reportSection);
}
