package com.github.fmcejudo.redlogs.card.converter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.TestCardQueryRequest;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator.CardQueryValidation;
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

  @Test
  void shouldFailOnUnprocessableCardQueries() {
    //Given
    cardConverter.register("test", TestCardQueryRequest::new);
    CardContext cardContext = CardContext.from("application", Map.of());
    CardFile cardFile = new CardFile(List.of(), LocalTime.of(8, 0), "24h", List.of(
        new CardQuery("id-one", "loki", "description-one", List.of(), Map.of()),
        new CardQuery("id-two", "prometheus", "description-two", List.of(), Map.of())
    ));

    // When
    Exception exception = Assertions.catchException(() -> cardConverter.convert(cardContext, cardFile));

    // Then
    Assertions.assertThat(exception).isInstanceOf(CardExecutionException.class)
        .hasMessageContaining("There are card queries with unknown processors:")
        .hasMessageContaining("(id -> 'id-one', processor -> 'loki')")
        .hasMessageContaining("(id -> 'id-two', processor -> 'prometheus')");
  }

  @Test
  void shouldFailWhenCardRequestValidationFails() {
    // Given
    CardQueryConverter cardQueryConverter = new CardQueryConverter() {
      @Override
      public CardQueryRequest convert(CardQuery cardQuery, CardMetadata cardMetadata) {
        return new TestCardQueryRequest(cardQuery, cardMetadata);
      }

      @Override
      public CardQueryValidator validator() {
        return cqr -> {
          if (cqr.id().equals("failCardQueryRequest")) {
            return CardQueryValidation.failed();
          } else {
            return CardQueryValidation.success();
          }
        };
      }
    };
    CardContext cardContext = CardContext.from("application", Map.of());
    cardConverter.register("test", cardQueryConverter);
    CardFile cardFile = new CardFile(List.of(), LocalTime.of(8, 0), "24h", List.of(
        new CardQuery("failCardQueryRequest", "test", "description-one", List.of(), Map.of()),
        new CardQuery("id-two", "test", "description-two", List.of(), Map.of())
    ));

    // When
    Exception exception = Assertions.catchException(() -> cardConverter.convert(cardContext, cardFile));


    // Then
    Assertions.assertThat(exception).isInstanceOf(CardExecutionException.class)
        .hasMessageContaining("validation for card requests have failed");
  }
}