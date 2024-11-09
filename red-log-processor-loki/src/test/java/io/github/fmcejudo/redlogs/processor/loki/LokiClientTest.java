package io.github.fmcejudo.redlogs.processor.loki;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)

@Testcontainers
@TestPropertySource(properties = {
    "logging.level.reactor.netty.http.client=DEBUG"
})
@Disabled
class LokiClientTest {

  static GenericContainer<?> lokiContainer = new GenericContainer<>(DockerImageName.parse("grafana/loki:2.9.5"))
      .withExposedPorts(3100);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    Startables.deepStart(lokiContainer).join();
    System.out.println(getLokiUrl(lokiContainer));
    registry.add("loki.url", () -> getLokiUrl(lokiContainer));
    registry.add("loki.username", () -> "username");
    registry.add("loki.password", () -> "password");
  }

  @Test
  void shouldDefineLokiConnection() {

    //Given && When && Then
    LokiConnectionDetails connectionDetails =
        LokiConnectionDetails.from(Map.of(
            "url", getLokiUrl(lokiContainer), "user", "username", "pass", "password", "datasource", "datasource"
        ));
    Assertions.assertThat(connectionDetails)
        .extracting("url", "user", "password")
        .containsExactly(getLokiUrl(lokiContainer), "username", "password");

  }

  private static String getLokiUrl(final GenericContainer<?> lokiContainer) {
    return "http://%s:%d".formatted(lokiContainer.getHost(), lokiContainer.getFirstMappedPort());
  }

}

