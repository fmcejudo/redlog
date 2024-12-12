package io.github.fmcejudo.redlogs.mongo.writer;

import java.util.UUID;

import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import io.github.fmcejudo.redlogs.report.domain.ExecutionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

class CardExecutionMongoWriter implements CardExecutionWriter {

  private static final Logger log = LoggerFactory.getLogger(CardExecutionMongoWriter.class);

  private MongoTemplate mongoTemplate;

  public CardExecutionMongoWriter(final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public String writeCardExecution(CardRequest cardRequest) {
    String executionId = UUID.randomUUID().toString();
    log.info("it starts execution {}", cardRequest.executionId());
    Execution execution = ExecutionBuilder
        .withExecutionIdAndApplication(executionId, cardRequest.applicationName())
        .withParameters(cardRequest.getParameters())
        .withReportDate(cardRequest.endTime().toLocalDate())
        .build();
    mongoTemplate.save(execution, "redlogExecutions");
    return executionId;
  }
}
