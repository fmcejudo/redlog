package io.github.fmcejudo.redlogs.loki.processor.connection;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory.QueryTypeEnum;
import io.github.fmcejudo.redlogs.loki.processor.connection.instant.QueryInstantClient;
import io.github.fmcejudo.redlogs.loki.processor.connection.range.QueryRangeClient;
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
class LokiClientTest {

  @Container
  static GenericContainer<?> lokiContainer = new GenericContainer(DockerImageName.parse("grafana/loki:2.9.5"))
      .withExposedPorts(3100);

  Map<String, String> connectionDetails;


  @BeforeEach
  void setUp() {
    connectionDetails = Map.of(
        "url", getLokiUrl(lokiContainer),
        "user", "username",
        "pass", "password",
        "datasource", "datasource"
    );
  }

  @DynamicPropertySource
  public static void configureProperties(DynamicPropertyRegistry registry) {
    Startables.deepStart(lokiContainer).join();
    System.out.println(getLokiUrl(lokiContainer));
    registry.add("loki.url", () -> getLokiUrl(lokiContainer));
    registry.add("loki.username", () -> "username");
    registry.add("loki.password", () -> "password");
  }

  @Test
  void shouldDefineLokiConnection() {

    //Given && When && Then
    LokiConnectionDetails lokiConnectionDetails = LokiConnectionDetails.from(connectionDetails);
    Assertions.assertThat(lokiConnectionDetails)
        .extracting("url", "user", "password")
        .containsExactly(getLokiUrl(lokiContainer), "username", "password");

  }

  @Test
  void shouldCreateALokiInstantClientFactory() {
    //Given
    LokiClientFactory lokiClientFactory = LokiClientFactory.createInstance(LokiConnectionDetails.from(connectionDetails));

    //When
    LokiClient lokiClient = lokiClientFactory.get(QueryTypeEnum.INSTANT);

    //Then
    Assertions.assertThat(lokiClient).isInstanceOf(QueryInstantClient.class);
    LokiResponse response = lokiClient.query(new LokiRequest("""
        {name="something"}\
        """, LocalDateTime.now().minusMinutes(3), LocalDateTime.now()));
    Assertions.assertThat(response.isSuccess()).isTrue();
  }

  @Test
  void shouldCreateALokiRangeClientFactory() {
    //Given
    LokiClientFactory lokiClientFactory = LokiClientFactory.createInstance(LokiConnectionDetails.from(connectionDetails));

    //When
    LokiClient lokiClient = lokiClientFactory.get(QueryTypeEnum.RANGE);

    //Then
    Assertions.assertThat(lokiClient).isInstanceOf(QueryRangeClient.class);
    LokiResponse response = lokiClient.query(new LokiRequest("""
        {name="something"}\
        """, LocalDateTime.now().minusMinutes(3), LocalDateTime.now()));
    Assertions.assertThat(response.isSuccess()).isTrue();
  }

  private static String getLokiUrl(final GenericContainer<?> lokiContainer) {
    return "http://%s:%d".formatted(lokiContainer.getHost(), lokiContainer.getFirstMappedPort());
  }

}

