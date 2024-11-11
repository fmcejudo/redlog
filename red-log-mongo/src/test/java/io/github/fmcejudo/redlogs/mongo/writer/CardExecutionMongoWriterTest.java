package io.github.fmcejudo.redlogs.mongo.writer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    CardMongoWriterConfiguration.class,
    RedlogMongoProperties.class
})
@ConfigurationPropertiesScan(basePackageClasses = RedlogMongoProperties.class)
@Testcontainers(disabledWithoutDocker = true)
@EnableAutoConfiguration
@TestPropertySource(properties = {
    "redlog.writer=mongo"
})
class CardExecutionMongoWriterTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7"));

  @Autowired
  CardExecutionWriter cardExecutionWriter;

  @Autowired
  @Qualifier("redlogMongoTemplate")
  MongoTemplate mongoTemplate;

  @DynamicPropertySource
  static void updateConfigSource(final DynamicPropertyRegistry registry) {
    Startables.deepStart(mongoDBContainer).join();
    mongoDBContainer.start();
    registry.add("redlog.writer.mongo.url", mongoDBContainer::getReplicaSetUrl);
  }

  @AfterAll
  static void onFinish() {
    mongoDBContainer.stop();
    mongoDBContainer.close();
  }

  @Test
  void shouldWriteAReportInDB() {
    //Given
    CardRequest cardRequest = new CardRequest(
        "appTest", LocalDate.now(), LocalDateTime.now().minusHours(1), LocalDateTime.now(), List.of(), Map.of()
    );

    //When
    String executionId = cardExecutionWriter.writeCardExecution(cardRequest);

    //Then
    Assertions.assertThat(executionId).isNotNull();
    List<Execution> executions = mongoTemplate.find(Query.query(Criteria.where("id").is(executionId)), Execution.class, "redlogExecutions");
    Assertions.assertThat(executions).hasSize(1);
  }
}