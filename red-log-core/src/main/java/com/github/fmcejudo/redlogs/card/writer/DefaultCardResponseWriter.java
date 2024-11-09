package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.execution.domain.Execution;
import com.github.fmcejudo.redlogs.report.domain.ReportSection;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
