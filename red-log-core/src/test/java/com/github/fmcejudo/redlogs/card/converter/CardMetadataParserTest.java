package com.github.fmcejudo.redlogs.card.converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.RangeParseException;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CardMetadataParserTest {

  CardMetadataParser cardMetadataParser;

  @BeforeEach
  void setUp() {
    this.cardMetadataParser = new CardMetadataParser();
  }

  @ParameterizedTest
  @ValueSource(strings = {"24h", "1440m"})
  @DisplayName("it should find start date and end date with a range of a day expressed in minutes and hours")
  void shouldCreateTimesInMetadata(String range) {
    //Given
    final LocalDate date = LocalDate.now();
    CardContext cardContext = CardContext.from("application", Map.of("date", date.format(DateTimeFormatter.ISO_DATE)));
    CardFile cardFile = new CardFile("", List.of(), LocalTime.of(8, 0, 0), range, List.of());

    //When
    CardMetadata cardMetadata = cardMetadataParser.parse(cardContext, cardFile);

    //Then
    Assertions.assertThat(cardMetadata.startTime()).isEqualTo(date.atTime(8, 0, 0).minusDays(1));
    Assertions.assertThat(cardMetadata.endTime()).isEqualTo(date.atTime(8, 0, 0));
  }

  @ParameterizedTest
  @ValueSource(strings = {"24d", "14am"})
  @DisplayName("it should catch exceptions transforming times")
  void shouldCatchWrongTimesInMetadata(String range) {
    //Given
    final LocalDate date = LocalDate.now();
    CardContext cardContext = CardContext.from("application", Map.of("date", date.format(DateTimeFormatter.ISO_DATE)));
    CardFile cardFile = new CardFile("", List.of(), LocalTime.of(8, 0, 0), range, List.of());

    //When && Then
    Assertions.assertThatThrownBy(() -> cardMetadataParser.parse(cardContext, cardFile)).isInstanceOf(RangeParseException.class);
  }

}