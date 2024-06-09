package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.writer.CardResponseMongoWriter.CardMongoRecord;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;

@ContextConfiguration(classes = {
        CardResponseMongoWriter.class
})
@EnableConfigurationProperties(value = RedLogMongoProperties.class)
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "redlog.mongo.collection-name-prefix=test"
})
@EnableMongoRepositories
@DataMongoTest
class CardResponseMongoWriterTest {


    @Autowired
    CardResponseWriter cardResponseWriter;

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

    @BeforeEach
    void setUp() {
        this.collectionName = MongoNamingUtils.composeCollectionName("test", "reports");
    }

    @Test
    void shouldSaveACardQueryResponse() {
        //Given
        CardQueryResponse cardQueryResponse = new CardQueryResponse(
                "application", LocalDate.now(), "section", "execution", "description", List.of(), "http://link", null
        );
        Query query = Query.query(Criteria.where("executionId").is("execution"));

        //When
        cardResponseWriter.write(cardQueryResponse);
        List<CardMongoRecord> cardMongoRecords = mongoTemplate.find(query, CardMongoRecord.class, collectionName);

        //Then
        Assertions.assertThat(cardMongoRecords).hasSize(1).allSatisfy(cardMongoRecord -> {
            Assertions.assertThat(cardMongoRecord.getId()).isEqualTo("section.execution");
            Assertions.assertThat(cardMongoRecord.getExecutionId()).isEqualTo("execution");
            Assertions.assertThat(cardMongoRecord.getReportId()).isEqualTo("section");
        });
    }

}