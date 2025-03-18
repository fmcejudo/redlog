package com.github.fmcejudo.redlogs.card.converter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.TestCardQueryRequest;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCardConverterTest {

  CardConverter cardConverter;

  @BeforeEach
  void setUp() {
    this.cardConverter = new DefaultCardConverter();
  }

  @Test
  void shouldRegisterConverters() {
    //Given && When && Then
    Assertions.assertThat(cardConverter.register("test", TestCardQueryRequest::new)).isTrue();
    Assertions.assertThat(cardConverter.register("test", TestCardQueryRequest::new)).isFalse();
  }

  @Test
  void shouldDeregisterConverters() {
    //Given && When && Then
    Assertions.assertThat(cardConverter.register("test", TestCardQueryRequest::new)).isTrue();
    Assertions.assertThat(cardConverter.deregister("test")).isTrue();
    Assertions.assertThat(cardConverter.deregister("test")).isFalse();
  }

  @Test
  void shouldConvertCard() {
    //Given
    final String processor = "test";
    cardConverter.register(processor, TestCardQueryRequest::new);
    CardContext cardContext = CardContext.from("application", Map.of());
    CardFile cardFile = new CardFile(List.of(), LocalTime.of(8, 0), "24h", List.of(
        new CardQuery("id-one", processor, "description-one", List.of(), Map.of()),
        new CardQuery("id-two", processor, "description-two", List.of(), Map.of())
    ));

    //When
    Iterator<CardQueryRequest> cardQueryRequests = cardConverter.convert(cardContext, cardFile);
    List<CardQueryRequest> result = new ArrayList<>();
    cardQueryRequests.forEachRemaining(result::add);

    Assertions.assertThat(result).hasSize(2).allSatisfy(cardQueryRequest -> {
      Assertions.assertThat(cardQueryRequest.id()).containsAnyOf("id-one", "id-two");
      Assertions.assertThat(cardQueryRequest.metadata()).satisfies(cardMetadata -> {
        Assertions.assertThat(cardMetadata.applicationName()).isEqualTo("application");
        Assertions.assertThat(cardMetadata.startTime()).isNotNull().isBefore(cardMetadata.endTime());
      });
    });

    //Then
    cardConverter.deregister(processor);
  }
}