package io.github.fmcejudo.redlogs.mongo.writer;

import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import io.github.fmcejudo.redlogs.report.domain.ReportSectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CardReportMongoWriter implements CardReportWriter {

  private static final Logger log = LoggerFactory.getLogger(CardReportMongoWriter.class);

  private final MongoTemplate mongoTemplate;

  public CardReportMongoWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void onNext(CardQueryResponse response) {
    log.info("next: {}", response);
    ReportSection reportSection = ReportSectionBuilder
        .fromExecutionIdAndReportId(response.executionId(), response.id())
        .withDescription(response.description())
        .withLink(response.link())
        .withItems(response.currentEntries())
        .build();
    mongoTemplate.save(reportSection, "redlogReports");
  }

  @Override
  public void onError(Throwable throwable) {
    log.error("error: {}", throwable.getMessage());
  }

  @Override
  public void onComplete() {
    log.info("completed");
  }

}
