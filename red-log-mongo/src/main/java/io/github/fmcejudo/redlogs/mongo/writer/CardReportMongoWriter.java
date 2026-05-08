package io.github.fmcejudo.redlogs.mongo.writer;

import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import io.github.fmcejudo.redlogs.report.domain.ReportSectionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CardReportMongoWriter implements CardReportWriter {

  private static final Logger log = LoggerFactory.getLogger(CardReportMongoWriter.class);

  private final MongoTemplate mongoTemplate;

  public CardReportMongoWriter(final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void onNext(CardQueryResponse response) {

    ReportSectionBuilder reportSectionBuilder = ReportSectionBuilder
        .fromExecutionIdAndReportId(response.executionId(), response.id())
        .withLink(response.link())
        .withDescription(response.description())
        .withItems(response.currentEntries())
        .withTags(response.tags());

    if (StringUtils.isNotBlank(response.error())) {
      reportSectionBuilder = reportSectionBuilder.withItems(List.of(new CardQueryResponseEntry(
          Map.of("error", response.error()), 1
      )));
    }

    log.info("next: {}", response);
    ReportSection reportSection = reportSectionBuilder.build();
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
