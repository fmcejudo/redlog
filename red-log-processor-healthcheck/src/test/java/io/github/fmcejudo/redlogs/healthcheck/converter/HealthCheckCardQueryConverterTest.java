package io.github.fmcejudo.redlogs.healthcheck.converter;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.healthcheck.HealthCheckRedlogPluginProvider;
import io.github.fmcejudo.redlogs.healthcheck.card.HealthCheckQueryRequest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.Test;

class HealthCheckCardQueryConverterTest {

  @Test
  void shouldCreateAHealthCheckCardQuery() {
    //Given
    final CardQuery cardQuery = new CardQuery(
        "health-check", "HEALTHCHECK", "health-check", Map.of("url", "http://url.io/health")
    );

    final CardMetadata metadata = new CardMetadata("60", "test-healthcheck", LocalDateTime.now(), LocalDateTime.now());

    CardQueryConverter cardQueryConverter = new HealthCheckRedlogPluginProvider().createCardQueryConverter();

    //When
    CardQueryRequest cardQueryRequest = cardQueryConverter.convert(cardQuery, metadata);

    //Then
    Assertions.assertThat(cardQueryRequest).isInstanceOf(HealthCheckQueryRequest.class)
        .asInstanceOf(InstanceOfAssertFactories.type(HealthCheckQueryRequest.class))
        .satisfies(hcqr -> {
          Assertions.assertThat(hcqr.url()).isEqualTo("http://url.io/health");
        });

  }

}