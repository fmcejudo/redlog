package com.github.fmcejudo.redlogs.card.engine;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.DefaultRedlogExecutionService.RedlogExecution;
import com.github.fmcejudo.redlogs.card.engine.exception.CardExecutionException;
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
        final String uuid = "uuid";
        CardContext cardContext = CardContext.from("TEST-SAVE", Map.of());

        //When
        redlogExecutionService.saveOrReplaceExecution(uuid, cardContext);
        RedlogExecution execution = mongoTemplate.findById(uuid, RedlogExecution.class, collectionName);


        //Then
        Assertions.assertThat(execution).isNotNull();
        Assertions.assertThat(execution.executionId()).isEqualTo(uuid);
        Assertions.assertThat(execution.reportDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(execution.status()).isEqualTo("PROCESSING");
    }

    @Test
    void shouldReplaceExecution() {

        //Given
        CardContext cardContext = CardContext.from("TEST-UPDATE", Map.of());

        //When
        redlogExecutionService.saveOrReplaceExecution("uuid-first", cardContext);
        RedlogExecution firstExecution = mongoTemplate.findById("uuid-first", RedlogExecution.class, collectionName);

        redlogExecutionService.saveOrReplaceExecution("uuid-second", cardContext);
        RedlogExecution secondExecution = mongoTemplate.findById("uuid-second", RedlogExecution.class, collectionName);
        RedlogExecution afterReplacedExec = mongoTemplate.findById("uuid-first", RedlogExecution.class, collectionName);

        //Then
        Assertions.assertThat(firstExecution).isNotNull()
                .satisfies(e -> Assertions.assertThat(e.executionId()).isEqualTo("uuid-first"));
        Assertions.assertThat(secondExecution).isNotNull()
                .satisfies(e -> Assertions.assertThat(e.executionId()).isEqualTo("uuid-second"));
        Assertions.assertThat(firstExecution.parameters()).isEqualTo(secondExecution.parameters());
        Assertions.assertThat(firstExecution.reportDate()).isEqualTo(secondExecution.reportDate());

        Assertions.assertThat(afterReplacedExec).isNull();
    }

    @Test
    void shouldRemovePreviousDataOnReplacing() {
        //Given
        CardContext cardContext = CardContext.from(
                "application",
                Map.of("date", LocalDate.now().minusDays(3).format(ISO_LOCAL_DATE), "param", "p1")
        );

        //When
        redlogExecutionService.saveOrReplaceExecution("uuid", cardContext);

        //Then
    }

    @Test
    void shouldUpdateExecutionStatus() {

        //Given
        CardContext cardContext = CardContext.from("TEST-STATUS", Map.of());

        //When
        redlogExecutionService.saveOrReplaceExecution("uuid-status", cardContext);
        redlogExecutionService.updateExecution("uuid-status", "SUCCESS");
        RedlogExecution execution = mongoTemplate.findById("uuid-status", RedlogExecution.class, collectionName);

        //Then
        Assertions.assertThat(execution).isNotNull().satisfies(e -> {
            Assertions.assertThat(e.executionId()).isEqualTo("uuid-status");
            Assertions.assertThat(e.status()).isEqualTo("SUCCESS");
        });

    }

    @Test
    void shouldFindExecutionById() {

        //Given
        CardContext cardContext = CardContext.from("TEST-FIND-BY-ID", Map.of());

        //When
        redlogExecutionService.saveOrReplaceExecution("uuid-find-id", cardContext);
        String executionId = redlogExecutionService.findExecutionId(cardContext);

        //Then
        Assertions.assertThat(executionId).isEqualTo("uuid-find-id");

    }

    @Test
    void shouldFailOnUnknownExecution() {

        //Given
        CardContext cardContext = CardContext.from("TEST-INVALID", Map.of());

        //When && Then
        Assertions.assertThatThrownBy(() -> redlogExecutionService.findExecutionId(cardContext))
                .isInstanceOf(CardExecutionException.class)
                .hasMessageContaining("There is no execution created for provided context");

    }

    @Test
    void shouldFailOnMultipleExecutions() {

        //Given
        CardContext cardContext = CardContext.from("TEST-FIND-BY-ID", Map.of());

        redlogExecutionService.saveOrReplaceExecution("uuid-find-id", cardContext);
        RedlogExecution execution = mongoTemplate.findById("uuid-find-id", RedlogExecution.class, collectionName);
        Assertions.assertThat(execution).isNotNull();

        RedlogExecution redlogExecution = new RedlogExecution(
                "uuid-find-duplicated", execution.applicationName(), execution.parameters(), execution.reportDate(), ""
        );
        mongoTemplate.save(redlogExecution, collectionName);

        //When && Then
        Assertions.assertThatThrownBy(() -> redlogExecutionService.findExecutionId(cardContext))
                .isInstanceOf(CardExecutionException.class)
                .hasMessageContaining("There are multiple execution for same context and it is inconsistent");
    }

}