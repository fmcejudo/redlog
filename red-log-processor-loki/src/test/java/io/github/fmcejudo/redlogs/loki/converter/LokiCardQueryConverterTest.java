package io.github.fmcejudo.redlogs.loki.converter;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.loki.LokiRedlogPluginProvider;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LokiCardQueryConverterTest {

  @Test
  void shouldCreateLokiCardQueryRequest() {
    //Given
    RedlogPluginProvider redlogPluginProvider = new LokiRedlogPluginProvider();
    CardQueryConverter cardQueryConverter = redlogPluginProvider.createCardQueryConverter();

    CardQuery cardQuery = new CardQuery("id", "LOKI", "description", Map.of("type", "count", "query", "{}"));
    CardMetadata metadata = new CardMetadata("20", "application-test", LocalDateTime.now().minusMinutes(3), LocalDateTime.now());

    //When
    CardQueryRequest cardQueryRequest = cardQueryConverter.convert(cardQuery, metadata);

    //Then
    Assertions.assertThat(cardQueryRequest).isInstanceOf(LokiCountCardRequest.class);
    Assertions.assertThat((LokiCountCardRequest)cardQueryRequest).satisfies(lccqr -> {
      Assertions.assertThat(lccqr.query()).isEqualTo("{}");
    });
  }
}