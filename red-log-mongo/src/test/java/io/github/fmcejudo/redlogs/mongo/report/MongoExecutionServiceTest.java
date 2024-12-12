package io.github.fmcejudo.redlogs.mongo.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import io.github.fmcejudo.redlogs.report.ExecutionService;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
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

@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = RedlogMongoProperties.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {
        MongoExecutionService.class
})
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "redlog.writer.mongo.collection-name-prefix=test"
})
class MongoExecutionServiceTest {

    private final String collectionName = "testExecutions";

    @Autowired
    ExecutionService executionService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedlogMongoProperties redlogMongoProperties;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5"));

    @DynamicPropertySource
    static void updateProperties(final DynamicPropertyRegistry registry) {
        Startables.deepStart(mongoDBContainer).join();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldFindAllExecutionForApplication() {
        //Given
        final String appName = "TEST";
        ExecutionGenerator executionGenerator =
                ExecutionGenerator.forAppName(appName).withParameters(Map.of("host", "localhost"));

        mongoTemplate.save(executionGenerator.withReportDate(LocalDate.now().minusDays(1)).generate(), collectionName);
        mongoTemplate.save(executionGenerator.generate(), collectionName);
        mongoTemplate.save(executionGenerator.withAppName("OTHER").generate(), collectionName);

        //When
        List<Execution> allExecutions = executionService.findExecutionWithParameters(appName, Map.of());

        //Then
        Assertions.assertThat(allExecutions).hasSize(2).allSatisfy(execution ->
                Assertions.assertThat(execution.application()).isEqualTo(appName)
        );
    }

    @Test
    void shouldFindAExecutionWithParametersMatching() {
        //Given
        final String appName = "TEST";
        ExecutionGenerator executionGenerator =
                ExecutionGenerator.forAppName(appName).withParameters(Map.of("host", "localhost"));

        mongoTemplate.remove(Query.query(new Criteria()), collectionName);
        mongoTemplate.save(
                executionGenerator.withParameters(Map.of("host", "localhost", "ip", "127.0.0.1")).generate(),
                collectionName
        );
        mongoTemplate.save(
                executionGenerator.withParameters(Map.of("host", "localhost", "ip", "192.168.1.34")).generate(),
                collectionName
        );
        mongoTemplate.save(
                executionGenerator.withParameters(Map.of("host", "remote")).generate(),
                collectionName
        );

        //When
        List<Execution> allExecutions =
                executionService.findExecutionWithParameters(appName, Map.of("host", "localhost"));

        //Then
        Assertions.assertThat(allExecutions).hasSize(2).allSatisfy(execution -> {
            Assertions.assertThat(execution.application()).isEqualTo(appName);
            Assertions.assertThat(execution.parameters()).containsEntry("host", "localhost");
        });
    }
}