package com.github.fmcejudo.redlogs.card;

import com.github.fmcejudo.redlogs.card.DefaultRedlogExecutionService.RedlogExecution;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@ContextConfiguration(classes = {
        DefaultRedlogExecutionService.class
})
@EnableConfigurationProperties(value = RedLogMongoProperties.class)
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "redlog.mongo.collection-name-prefix=test"
})
@EnableMongoRepositories
@DataMongoTest
class RedlogExecutionServiceTest {

    @Autowired
    RedlogExecutionService redlogExecutionService;

    @Autowired
    MongoTemplate mongoTemplate;

    private String collectionName;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5"));

    @DynamicPropertySource
    static void updateContextProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(mongoDBContainer).join();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.close();
    }

    @BeforeEach
    void setUp() {
        this.collectionName = MongoNamingUtils.composeCollectionName("test", "executions");
    }

    @Test
    void shouldSaveAExecution() {
        //Given
        CardContext cardContext = CardContext.from("TEST-SAVE", Map.of());

        //When
        String executionId = redlogExecutionService.saveExecution(cardContext);
        RedlogExecution execution = mongoTemplate.findById(executionId, RedlogExecution.class, collectionName);

        //Then
        Assertions.assertThat(execution).isNotNull();
        Assertions.assertThat(execution.executionId()).isEqualTo(executionId);
        Assertions.assertThat(execution.reportDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(execution.status()).isEqualTo("PROCESSING");
        Assertions.assertThat(executionId).startsWith("test-save").contains(LocalDate.now().format(ISO_LOCAL_DATE));
    }

    @Test
    void shouldUpdateExecutionStatus() {

        //Given
        CardContext cardContext = CardContext.from("TEST-STATUS", Map.of());

        //When
        String executionId = redlogExecutionService.saveExecution(cardContext);
        redlogExecutionService.updateExecution(cardContext, "SUCCESS");
        RedlogExecution execution = mongoTemplate.findById(executionId, RedlogExecution.class, collectionName);

        //Then
        Assertions.assertThat(execution).isNotNull().satisfies(e -> {
            Assertions.assertThat(e.executionId()).isEqualTo(executionId);
            Assertions.assertThat(e.status()).isEqualTo("SUCCESS");
        });
    }
}