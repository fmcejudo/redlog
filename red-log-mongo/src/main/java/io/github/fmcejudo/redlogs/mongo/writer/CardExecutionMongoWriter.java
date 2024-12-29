package io.github.fmcejudo.redlogs.mongo.writer;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
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
  public String writeCardExecution(CardMetadata metadata, Map<String, String> parameters) { // executionId, applicationName, reportDate, parameters
    log.info("it starts execution {}", metadata.executionId());
    Execution execution = ExecutionBuilder
        .withExecutionIdAndApplication(metadata.executionId(), metadata.applicationName())
        .withParameters(parameters)
        .withReportDate(metadata.endTime().toLocalDate())
        .build();
    mongoTemplate.save(execution, "redlogExecutions");
    return metadata.executionId();
  }
}
