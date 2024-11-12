package io.github.fmcejudo.redlogs.processor.loki;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardType;
import io.github.fmcejudo.redlogs.processor.loki.instant.QueryInstantClient;
import io.github.fmcejudo.redlogs.processor.loki.range.QueryRangeClient;
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

  private final CardQueryRequest.CardQueryContext EMPTY_QUERY_CONTEXT =
      new CardQueryRequest.CardQueryContext(null, null, null, null);

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
    //Given
    final CardType cardType = CardType.SUMMARY;

    //When
    LokiClient lokiClient = lokiClientFactory.get(CardQueryRequest.getInstance(cardType, EMPTY_QUERY_CONTEXT));

    //Then
    Assertions.assertThat(lokiClient).isInstanceOf(QueryRangeClient.class);
  }

  @Test
  void shouldCreateInstantClientOnCount() {
    //Given
    final CardType cardType = CardType.COUNT;

    //When
    LokiClient lokiClient = lokiClientFactory.get(CardQueryRequest.getInstance(cardType, EMPTY_QUERY_CONTEXT));

    //Then
    Assertions.assertThat(lokiClient).isInstanceOf(QueryInstantClient.class);
  }

}