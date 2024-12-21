package io.github.fmcejudo.redlogs.mongo.writer;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;

import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import io.github.fmcejudo.redlogs.mongo.RedLogMongoConfiguration;
import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
    RedLogMongoConfiguration.class,
    RedlogMongoProperties.class,
    CardMongoWriterConfiguration.class
})
@ConfigurationPropertiesScan(basePackageClasses = RedlogMongoProperties.class)
@Testcontainers(disabledWithoutDocker = true)
@EnableAutoConfiguration
@TestPropertySource(properties = {
    "redlog.writer.type=mongo"
})
class CardReportMongoWriterTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7"));

  @Autowired
  CardReportWriter cardReportWriter;

  @Autowired
  @SpyBean
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
    CardQueryResponse cardQueryResponse =
        CardQueryResponse.success(LocalDate.now(), "id", "executionId", "description", "http://link", List.of());

    //When && Then
    List<ReportSection> reportSections =
        mongoTemplate.find(Query.query(Criteria.where("reportId").is("id")), ReportSection.class, "redlogReports");
    Assertions.assertThat(reportSections).isEmpty();

    cardReportWriter.onNext(cardQueryResponse);

    reportSections = mongoTemplate.find(Query.query(Criteria.where("reportId").is("id")), ReportSection.class, "redlogReports");
    Assertions.assertThat(reportSections).hasSize(1);
  }

  @Test
  void shouldCompleteWrite() {
    //Given && When
    cardReportWriter.onComplete();

    //Then
    Mockito.verify(mongoTemplate, Mockito.never()).save(any());
  }


  @Test
  void shouldManageErrors() {
    //Given && When
    cardReportWriter.onError(new RuntimeException("error"));

    //Then
    Mockito.verify(mongoTemplate, Mockito.never()).save(any());
  }
}