package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.loki.LokiRedlogPluginProvider;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
    "logging.level.reactor.netty.http.client=DEBUG"
})
class LokiCardQueryProcessorTest {

  @Container
  static GenericContainer<?> lokiContainer = new GenericContainer(DockerImageName.parse("grafana/loki:2.9.5"))
      .withExposedPorts(3100);

  Map<String, String> connectionDetails;

  RedlogPluginProvider redlogPluginProvider;

  @BeforeEach
  void setUp() {
    this.redlogPluginProvider = new LokiRedlogPluginProvider();
    connectionDetails = Map.of(
        "loki.url", getLokiUrl(lokiContainer),
        "loki.user", "username",
        "loki.pass", "password",
        "loki.datasource", "default",
        "loki.dashboardUrl", "http://localhost:3000"
    );
  }

  @DynamicPropertySource
  public static void configureProperties(DynamicPropertyRegistry registry) {
    Startables.deepStart(lokiContainer).join();
    registry.add("loki.url", () -> getLokiUrl(lokiContainer));
    registry.add("loki.username", () -> "username");
    registry.add("loki.password", () -> "password");
  }

  @Test
  void shouldCreateQueryProcessor() {
    //Given
    CardQuery cardQuery = new CardQuery("id", "LOKI", "description", Map.of(
        "type", "count",
        "query", """
            count_over_time({name="something"}[24h])\
            """));
    CardMetadata metadata = new CardMetadata("20", "test", LocalDateTime.now().minusMinutes(3), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = LokiCountCardRequest.from(cardQuery, metadata);
    CardQueryProcessor cardQueryProcessor = redlogPluginProvider.createProcessor(connectionDetails);

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.id()).isEqualTo("id");
    Assertions.assertThat(response.link()).contains("http://localhost:3000/explore")
        .contains("%22datasource%22:%22default%22")
        .contains("queries");
  }

  @Test
  void shouldCreateQueryProcessorWithoutLink() {
    //Given

    Map<String, String> connectionDetails = Map.of(
        "loki.url", getLokiUrl(lokiContainer),
        "loki.user", "username",
        "loki.pass", "password");

    CardQuery cardQuery = new CardQuery("id", "LOKI", "description", Map.of(
        "type", "count",
        "query", """
            count_over_time({name="something"}[24h])\
            """));
    CardMetadata metadata = new CardMetadata("20", "test", LocalDateTime.now().minusMinutes(3), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = LokiCountCardRequest.from(cardQuery, metadata);
    CardQueryProcessor cardQueryProcessor = redlogPluginProvider.createProcessor(connectionDetails);

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.id()).isEqualTo("id");
    Assertions.assertThat(response.link()).isNull();
  }

  private static String getLokiUrl(final GenericContainer<?> lokiContainer) {
    return "http://%s:%d".formatted(lokiContainer.getHost(), lokiContainer.getFirstMappedPort());
  }

}