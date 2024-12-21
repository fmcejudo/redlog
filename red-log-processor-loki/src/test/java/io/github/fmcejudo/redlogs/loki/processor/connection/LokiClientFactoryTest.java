package io.github.fmcejudo.redlogs.loki.processor.connection;

import java.util.Map;

import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory.QueryTypeEnum;
import io.github.fmcejudo.redlogs.loki.processor.connection.instant.QueryInstantClient;
import io.github.fmcejudo.redlogs.loki.processor.connection.range.QueryRangeClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
    "redlog.loki.url=http://localhost:3100",
    "redlog.loki.username=username",
    "redlog.loki.password=password"
})
class LokiClientFactoryTest {

  private LokiClientFactory lokiClientFactory;

  @BeforeEach
  void setUp() {
    Map<String, String> lokiConfigParams = Map.of(
        "url", "http://localhost:8080/loki",
        "datasource", "my-datasource"
    );
    this.lokiClientFactory = LokiClientFactory.createInstance(LokiConnectionDetails.from(lokiConfigParams));
  }

  @Test
  void shouldCreateRangeClientOnSummary() {
    //Given && When
    LokiClient lokiClient = lokiClientFactory.get(QueryTypeEnum.RANGE);

    //Then
    Assertions.assertThat(lokiClient).isInstanceOf(QueryRangeClient.class);
  }

  @Test
  void shouldCreateInstantClientOnCount() {
    //Given && When
    LokiClient lokiClient = lokiClientFactory.get(QueryTypeEnum.INSTANT);

    //Then
    Assertions.assertThat(lokiClient).isInstanceOf(QueryInstantClient.class);
  }

}