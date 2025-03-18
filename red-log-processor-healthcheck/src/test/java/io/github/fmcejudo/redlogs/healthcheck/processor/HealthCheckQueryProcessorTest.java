package io.github.fmcejudo.redlogs.healthcheck.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.healthcheck.HealthCheckRedlogPluginProvider;
import io.github.fmcejudo.redlogs.healthcheck.card.HealthCheckQueryRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@Testcontainers(disabledWithoutDocker = true)
class HealthCheckQueryProcessorTest {

  static MockServerContainer mockServer = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));

  static MockServerClient mockServerClient;

  CardQuery cardQuery;

  CardMetadata metadata;

  @DynamicPropertySource
  static void updateConfig(DynamicPropertyRegistry registry) {
    Startables.deepStart(mockServer).join();
    mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
  }

  @BeforeEach
  void setUp() {
    mockServerClient.reset();

    cardQuery = new CardQuery(
        "health-check", "HEALTHCHECK", "health-check", List.of(), Map.of("url", mockServer.getEndpoint().concat("/health"))
    );
    metadata = new CardMetadata("50", "test", LocalDateTime.now(), LocalDateTime.now());
  }

  @Test
  void shouldQueryForHealthcheckDown() {
    //Given
    CardQueryRequest cardQueryRequest = new HealthCheckQueryRequest(cardQuery, metadata);
    mockServerClient
        .when(HttpRequest.request().withMethod("GET")
            .withContentType(MediaType.APPLICATION_JSON)
            .withPath("/health"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withContentType(MediaType.APPLICATION_JSON)
            .withBody(
                """
                    { "status" : "DOWN" }\
                    """));

    CardQueryProcessor cardQueryProcessor = new HealthCheckRedlogPluginProvider().createProcessor(Map.of());

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.currentEntries()).hasSize(1);
    Assertions.assertThat(response.error()).isNull();
  }

  @Test
  void shouldQueryForHealthcheckUp() {
    //Given
    CardQueryRequest cardQueryRequest = new HealthCheckQueryRequest(cardQuery, metadata);
    mockServerClient
        .when(HttpRequest.request().withMethod("GET")
            .withContentType(MediaType.APPLICATION_JSON)
            .withPath("/health"))
        .respond(HttpResponse.response().withStatusCode(200)
            .withContentType(MediaType.APPLICATION_JSON)
            .withBody(
                """
                    { "status" : "UP" }\
                    """));

    CardQueryProcessor cardQueryProcessor = new HealthCheckRedlogPluginProvider().createProcessor(Map.of());

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.currentEntries()).isEmpty();
    Assertions.assertThat(response.error()).isNull();
  }

  @Test
  void shouldDealWithOtherResponseStatus() {
    //Given
    CardQueryRequest cardQueryRequest = new HealthCheckQueryRequest(cardQuery, metadata);
    mockServerClient
        .when(HttpRequest.request().withMethod("GET")
            .withContentType(MediaType.APPLICATION_JSON)
            .withPath("/health"))
        .respond(HttpResponse.response().withStatusCode(404)
            .withContentType(MediaType.APPLICATION_JSON));

    CardQueryProcessor cardQueryProcessor = new HealthCheckRedlogPluginProvider().createProcessor(Map.of());

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.currentEntries()).isEmpty();
    Assertions.assertThat(response.error()).isNotNull();
  }

}