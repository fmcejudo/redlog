package io.github.fmcejudo.redlogs.processor.loki.filter;

import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.domain.CardType;
import io.github.fmcejudo.redlogs.card.processor.filter.ResponseEntryFilter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ResponseEntryFilterTest {

  @Test
  void shouldFilterCounterGreaterThan() {
    //Given
    final int expectedAtLeast = 3;
    CardQueryRequest.CardQueryContext context =
        new CardQueryRequest.CardQueryContext("count-filtering", "LOKI", "counter-filtering", "{}", expectedAtLeast);
    CardQueryRequest cardQueryRequest = CardQueryRequest.getInstance(CardType.COUNT, context);

    ResponseEntryFilter entryFilter = ResponseEntryFilter.getInstance(cardQueryRequest);

    List<CardQueryResponseEntry> entries = List.of(
        new CardQueryResponseEntry(Map.of("entry", "one"), 2),
        new CardQueryResponseEntry(Map.of("entry", "two"), 0),
        new CardQueryResponseEntry(Map.of("entry", "three"), 1),
        new CardQueryResponseEntry(Map.of("entry", "four"), 3),
        new CardQueryResponseEntry(Map.of("entry", "five"), 5)
    );

    //When
    List<CardQueryResponseEntry> records = entries.stream().filter(entryFilter::filter).toList();

    //Then
    Assertions.assertThat(records).hasSize(2).allMatch(r -> r.count() >= expectedAtLeast);
  }

  @Test
  void shouldSummaryNotFiltering() {
    //Given
    final int expectedAtLeast = 3;
    CardQueryRequest.CardQueryContext context =
        new CardQueryRequest.CardQueryContext("count-filtering", "LOKI", "counter-filtering", "{}", expectedAtLeast);
    CardQueryRequest cardQueryRequest = CardQueryRequest.getInstance(CardType.SUMMARY, context);

    ResponseEntryFilter entryFilter = ResponseEntryFilter.getInstance(cardQueryRequest);

    List<CardQueryResponseEntry> entries = List.of(
        new CardQueryResponseEntry(Map.of("entry", "one"), 2),
        new CardQueryResponseEntry(Map.of("entry", "two"), 0),
        new CardQueryResponseEntry(Map.of("entry", "three"), 1),
        new CardQueryResponseEntry(Map.of("entry", "four"), 3),
        new CardQueryResponseEntry(Map.of("entry", "five"), 5)
    );

    //When
    List<CardQueryResponseEntry> records = entries.stream().filter(entryFilter::filter).toList();

    //Then
    Assertions.assertThat(records).hasSize(5);
  }

  @Test
  void shouldNotFilterOnUnknownThreshold() {
    //Given
    CardQueryRequest.CardQueryContext context =
        new CardQueryRequest.CardQueryContext("count-filtering", "LOKI", "counter-filtering", "{}");
    CardQueryRequest cardQueryRequest = CardQueryRequest.getInstance(CardType.COUNT, context);

    ResponseEntryFilter entryFilter = ResponseEntryFilter.getInstance(cardQueryRequest);

    List<CardQueryResponseEntry> entries = List.of(
        new CardQueryResponseEntry(Map.of("entry", "one"), 2),
        new CardQueryResponseEntry(Map.of("entry", "two"), 0),
        new CardQueryResponseEntry(Map.of("entry", "three"), 1),
        new CardQueryResponseEntry(Map.of("entry", "four"), 3),
        new CardQueryResponseEntry(Map.of("entry", "five"), 5)
    );

    //When
    List<CardQueryResponseEntry> records = entries.stream().filter(entryFilter::filter).toList();

    //Then
    Assertions.assertThat(records).hasSize(4).allMatch(r -> r.count() >= 1);
  }

}