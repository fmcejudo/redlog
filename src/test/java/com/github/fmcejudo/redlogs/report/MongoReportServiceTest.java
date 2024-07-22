package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.execution.domain.Execution;
import com.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.domain.ReportItem;
import com.github.fmcejudo.redlogs.report.domain.ReportSection;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.assertj.core.api.Assertions;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
@EnableConfigurationProperties(value = RedLogMongoProperties.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {
        MongoReportService.class
})
@TestPropertySource(properties = {
        "redlog.mongo.collection-name-prefix=test"
})
class MongoReportServiceTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5"));

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReportService reportService;

    @DynamicPropertySource
    static void updateProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(mongoDBContainer).join();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldComposeAReport() {
        //Given
        final String executionId = "id";
        String executionsCollectionName = MongoNamingUtils.composeCollectionName("test", "executions");
        Execution execution = new Execution(executionId, "app", Map.of(), LocalDate.now());
        mongoTemplate.save(execution, executionsCollectionName);

        String reportsCollectionName = MongoNamingUtils.composeCollectionName("test", "reports");

        ReportSection reportSection = new ReportSection(
                executionId,
                "section-id",
                "sectionId",
                "http://grafana.link",
                List.of(new ReportItem(Map.of("environment", "pre"), 10))
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

    }
}