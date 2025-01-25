package io.github.fmcejudo.redlogs.processor;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fmcejudo.redlogs.MongoRedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers(disabledWithoutDocker = true)
class MongoCardQueryProcessorTest {

  static GenericContainer container = new GenericContainer(DockerImageName.parse("mongo:7"))
      .withExposedPorts(27017)
      .withCopyToContainer(MountableFile.forClasspathResource("./init-schema.js"), "/docker-entrypoint-initdb.d/init-script.js");

  CardQueryProcessor cardQueryProcessor;

  @BeforeAll
  static void onInit() {
    container.start();
  }

  @AfterAll
  static void onFinish() {
    container.stop();
  }

  @BeforeEach
  void setUp() {
    Map<String, String> properties = Map.of(
        "mongo.host", container.getHost(),
        "mongo.port", String.valueOf(container.getFirstMappedPort()),
        "mongo.database", "characters",
        "mongo.user", "test_container",
        "mongo.pass", "test_container"
    );

    this.cardQueryProcessor = new MongoRedlogPluginProvider().createProcessor(properties);
  }

  @Test
  void shouldProcessAMongoCountCard() {
    //Given
    CardQuery cardQuery = new CardQuery(
        "mongo-count-card", "MONGO", "mongo-count-card",
        Map.of("type", "COUNT", "query", """
            {"role" : "Sith Lord"}
            """, "collection", "characters", "fields", "name")
    );

    CardMetadata cardMetadata = new CardMetadata("40", "test", LocalDateTime.now(), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = new MongoCountCardRequest(cardQuery, cardMetadata);

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.executionId()).isEqualTo("40");
    Assertions.assertThat(response.error()).isNull();
    Assertions.assertThat(response.currentEntries()).hasSize(2);
  }

  @Test
  void shouldProcessAListCountCard() {
    //Given
    CardQuery cardQuery = new CardQuery("mongo-list-card", "MONGO", "mongo-list-card",
        Map.of("type", "LIST", "query", """
            {"role": "Sith Lord"}\
            """, "collection", "characters", "fields", "name")
    );
    CardMetadata cardMetadata = new CardMetadata("40", "test", LocalDateTime.now(), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = new MongoListCardRequest(cardQuery, cardMetadata);

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.executionId()).isEqualTo("40");
    Assertions.assertThat(response.error()).isNull();
    Assertions.assertThat(response.currentEntries()).hasSize(2);
  }

  @Test
  void shouldCatchExceptions() {
    //Given
    CardQuery cardQuery = new CardQuery("mongo-list-card", "MONGO", "mongo-list-card",
        Map.of("type", "LIST", "query", """
            {"role: "Sith Lord"}\
            """, "collection", "characters", "fields", "name")
    );
    CardMetadata cardMetadata = new CardMetadata("40", "test", LocalDateTime.now(), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = new MongoListCardRequest(cardQuery, cardMetadata);

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.executionId()).isEqualTo("40");
    Assertions.assertThat(response.error()).contains("JSON reader was expecting");
    Assertions.assertThat(response.currentEntries()).hasSize(0);
  }

}