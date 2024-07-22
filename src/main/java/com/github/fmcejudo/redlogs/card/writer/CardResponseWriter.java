package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.execution.domain.Execution;
import com.github.fmcejudo.redlogs.report.domain.ReportItem;
import com.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CardResponseWriter {

    void writeExecution(CardRequest cardRequest);

    void onNext(CardQueryResponse cardTaskResult);

    void onError(Throwable throwable);

    void onComplete();
}

class DefaultCardResponseWriter implements CardResponseWriter {

    private static final Logger log = LoggerFactory.getLogger(DefaultCardResponseWriter.class);

    public DefaultCardResponseWriter(final CardExecutionAppender cardExecutionAppender,
                                     final CardReportAppender cardReportAppender) {
        this.cardExecutionAppender = cardExecutionAppender;
        this.cardReportAppender = cardReportAppender;
    }

    private final CardExecutionAppender cardExecutionAppender;
    private final CardReportAppender cardReportAppender;

    @Override
    public void onNext(CardQueryResponse response) {
        log.info("next: {}", response);
        ReportSection reportSection = ReportSectionBuilder
                .fromExecutionIdAndReportId(response.executionId(), response.id())
                .withDescription(response.description())
                .withLink(response.link())
                .withItems(response.currentEntries())
                .build();
        cardReportAppender.add(reportSection);

    }

    @Override
    public void onError(Throwable throwable) {
        log.error("error: {}", throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("completed");
    }

    @Override
    public void writeExecution(final CardRequest cardRequest) {
        log.info("it starts execution {}", cardRequest.executionId());
        Execution execution = ExecutionBuilder
                .withExecutionIdAndApplication(cardRequest.executionId(), cardRequest.applicationName())
                .withParameters(cardRequest.getParameters())
                .withReportDate(cardRequest.endTime().toLocalDate())
                .build();
        cardExecutionAppender.add(execution);
    }

}

@FunctionalInterface
interface ExecutionBuilder {

    public Execution build();

    public static ExecutionBuilder withExecutionIdAndApplication(final String executionId, final String application) {
        return () -> new Execution(executionId, application, Map.of(), LocalDate.now());
    }

    default ExecutionBuilder withParameters(final Map<String, String> parameters) {
        return () -> {
            Execution e = this.build();
            return new Execution(e.id(), e.application(), parameters, e.reportDate());
        };
    }

    default ExecutionBuilder withReportDate(final LocalDate reportDate) {
        return () -> {
            Execution e = this.build();
            return new Execution(e.id(), e.application(), e.parameters(), reportDate);
        };
    }
}


@FunctionalInterface
interface ReportSectionBuilder {

    public ReportSection build();

    static ReportSectionBuilder fromExecutionIdAndReportId(final String executionId, final String reportId) {
        String id = String.join(".", reportId, executionId);
        return () -> new ReportSection(id, executionId, reportId, null, null, List.of());
    }

    default ReportSectionBuilder withDescription(final String description) {
        return () -> {
            ReportSection r = this.build();
            return new ReportSection(r.id(), r.executionId(), r.reportId(), description, r.link(), r.items());
        };
    }

    default ReportSectionBuilder withLink(final String link) {
        return () -> {
            ReportSection r = this.build();
            return new ReportSection(r.id(), r.executionId(), r.reportId(), r.description(), link, r.items());
        };
    }

    default ReportSectionBuilder withItems(final List<CardQueryResponseEntry> items) {
        return () -> {
            ReportSection r = this.build();
            var reportItems = items.stream().map(cqr -> new ReportItem(cqr.labels(), cqr.count())).toList();
            return new ReportSection(r.id(), r.executionId(), r.reportId(), r.description(), r.link(), reportItems);
        };
    }
}
