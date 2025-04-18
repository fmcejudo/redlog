package io.github.fmcejudo.redlogs.mongo.report;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.mongo.MongoNamingUtils;
import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.ReportService;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportItem;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
@EnableConfigurationProperties(value = RedlogMongoProperties.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {
    MongoReportService.class,
    RedLogMongoReportConfiguration.class
})
@TestPropertySource(properties = {
    "redlog.writer.mongo.collection-name-prefix=test"
})
class MongoReportServiceTest {

  @Container
  static CustomMongoDBContainer mongoDBContainer = CustomMongoDBContainer.fromMongoVersion("5");

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ReportService reportService;

  @DynamicPropertySource
  static void updateProperties(DynamicPropertyRegistry registry) {
    Startables.deepStart(mongoDBContainer).join();
    mongoDBContainer.start();
    mongoDBContainer.updateConfig(registry);
  }

  @AfterAll
  static void onFinish() {
    mongoDBContainer.stop();
    mongoDBContainer.close();
  }

  @Test
  void shouldComposeAReport() {
    //Given
    final String executionId = "id";
    String executionsCollectionName = MongoNamingUtils.composeCollectionName("test", "executions");
    Execution execution =
        new Execution(executionId, "app", Map.of(), LocalDate.now(), LocalDateTime.now().minusDays(4));
    mongoTemplate.save(execution, executionsCollectionName);

    String reportsCollectionName = MongoNamingUtils.composeCollectionName("test", "reports");

    ReportSection reportSection = new ReportSection(
        executionId,
        "section-id",
        "sectionId",
        "http://grafana.link",
        List.of(new ReportItem(Map.of("environment", "pre"), 10)),
        LocalDateTime.now().minusDays(4),
        List.of("important")
    );
    mongoTemplate.save(reportSection, reportsCollectionName);

    //When
    Report report = reportService.findReport(executionId);

    //Then
    Assertions.assertThat(report.applicationName()).isEqualTo("app");
    Assertions.assertThat(report.reportDate()).isEqualTo(LocalDate.now());
    Assertions.assertThat(report.sections()).hasSize(1).first().satisfies(rs -> {
      Assertions.assertThat(rs.reportId()).isEqualTo("section-id");
      Assertions.assertThat(rs.description()).isEqualTo("sectionId");
      Assertions.assertThat(rs.items()).hasSize(1).first().satisfies(ri -> {
        Assertions.assertThat(ri.labels()).containsEntry("environment", "pre");
        Assertions.assertThat(ri.count()).isEqualTo(10L);
      });
    });

    Awaitility.await().atMost(Duration.ofMinutes(5))
        .until(() -> reportService.findReport(executionId) == null);

  }
}

class CustomMongoDBContainer extends GenericContainer<CustomMongoDBContainer> {

  private static final String DATABASE_NAME = "redlog";

  public CustomMongoDBContainer(DockerImageName dockerImageName) {
    super(dockerImageName);
    this.withExposedPorts(27017)
        .withCopyToContainer(
            MountableFile.forClasspathResource("db/mongo/redlog-mongo.js"),
            "/docker-entrypoint-initdb.d/redlog-mongo.js"
        )
        .withEnv("MONGO_INITDB_DATABASE", "admin")
        .withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
        .withEnv("MONGO_INITDB_ROOT_PASSWORD", "pass")
        .waitingFor(Wait.forLogMessage("(?i).*waiting for connections.*", 2))
        .withStartupTimeout(Duration.ofSeconds(10));
  }

  public static CustomMongoDBContainer fromMongoVersion(final String version) {
    return new CustomMongoDBContainer(DockerImageName.parse("mongo:" + version));
  }

  public String getReplicaSetUrl() {
    if (!isRunning()) {
      throw new IllegalStateException("MongoDBContainer should be started first");
    }
    String host = DockerClientFactory.instance().dockerHostIpAddress();
    return "mongodb://%s:%s@%s:%d/%s".formatted("test", "test", host, this.getMappedPort(27017), DATABASE_NAME);
  }

  public void updateConfig(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", this::getReplicaSetUrl);
  }
}