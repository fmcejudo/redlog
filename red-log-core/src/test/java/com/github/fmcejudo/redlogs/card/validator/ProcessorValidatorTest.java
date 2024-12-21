package com.github.fmcejudo.redlogs.card.validator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardQuery;
import org.junit.jupiter.api.Test;

class ProcessorValidatorTest {

  final BiFunction<String, String, CardQuery> cardQueryBuilder = (id, processor) ->
      new CardQuery(id, processor, "description", Map.of());

  final CardContext cardContext = CardContext.from("sample", Map.of());

  @Test
  void shouldBeValidProcessors() {
    //Given
    CardFile cardFile = new CardFile("", List.of(), LocalTime.now(), "24h", List.of(
        cardQueryBuilder.apply("valid-one", "LOKI"),
        cardQueryBuilder.apply("valid-two", "MONGO"),
        cardQueryBuilder.apply("valid-three", "BEAN")
    ));

    //When
    CardValidation cardValidation = new ProcessorValidator().validateOn(cardFile, cardContext);

    //Then
    assertThat(cardValidation.isSuccess()).isTrue();
  }

  @Test
  void shouldBeInvalidProcessors() {
    //Given
    CardFile cardFile = new CardFile("", List.of(), LocalTime.now(), "24h", List.of(
        cardQueryBuilder.apply("valid-one", "LOKI"),
        cardQueryBuilder.apply("valid-two", null),
        cardQueryBuilder.apply("valid-three", "")
    ));

    //When
    CardValidation cardValidation = new ProcessorValidator().validateOn(cardFile, cardContext);

    //Then
    assertThat(cardValidation.isSuccess()).isFalse();
    assertThat(cardValidation.errors())
        .contains("card query with id valid-two needs to define processor")
        .contains("card query with id valid-three needs to define processor");
  }

  @Test
  void shouldBeValidOnEmptyQueries() {

    //Given
    CardFile cardFile = new CardFile("", List.of(), LocalTime.now(), "24h", List.of());

    //When
    CardValidation cardValidation = new ProcessorValidator().validateOn(cardFile, cardContext);

    //Then
    assertThat(cardValidation.isSuccess()).isTrue();
  }

}