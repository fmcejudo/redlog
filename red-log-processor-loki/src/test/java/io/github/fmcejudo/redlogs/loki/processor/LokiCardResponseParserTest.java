package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LokiCardResponseParserTest {

  LokiResponseGenerator lokiResponseGenerator;

  @BeforeEach
  void setUp() {
    this.lokiResponseGenerator = LokiResponseGenerator.status(true)
        .addResult(Map.of("error one", "error message one"), 1)
        .addResult(Map.of("error two", "error message two"), 1);

  }

  @Test
  void shouldCreateALokiCountParser() {
    //Given && When
    LokiCardResponseParser<LokiCountCardRequest> parser = LokiCardResponseParser.createParser(LokiCountCardRequest.class);

    //Then
    Assertions.assertThat(parser).isInstanceOf(CountCardResponseParser.class);
  }

  @Test
  void shouldCreateALokiSummaryParser() {
    //Given && When
    LokiCardResponseParser<LokiSummaryCardRequest> parser = LokiCardResponseParser.createParser(LokiSummaryCardRequest.class);

    //Then
    Assertions.assertThat(parser).isInstanceOf(SummaryCardResponseParser.class);
  }

  @Test
  void shouldCreateACountResponse() {
    //Given
    LokiCardResponseParser<LokiCountCardRequest> parser = LokiCardResponseParser.createParser(LokiCountCardRequest.class);
    LokiResponse lokiResponse = lokiResponseGenerator.generate();

    LokiCountCardRequest cardRequest = LokiCountCardRequest.from(
        new CardQuery("summary-id", "LOKI", "test", List.of(), Map.of("type", "count")),
        new CardMetadata("20", "test", LocalDateTime.now().minusDays(1), LocalDateTime.now())
    );

    //When
    CardQueryResponse response = parser.parse(lokiResponse, cardRequest);

    //Then
    Assertions.assertThat(response.currentEntries()).hasSize(2);
  }

  @Test
  void shouldCreateASummaryResponse() {
    //Given
    LokiCardResponseParser<LokiSummaryCardRequest> parser = LokiCardResponseParser.createParser(LokiSummaryCardRequest.class);
    LokiResponse lokiResponse = lokiResponseGenerator.generate();

    LokiSummaryCardRequest cardRequest = LokiSummaryCardRequest.from(
        new CardQuery("summary-id", "LOKI", "test", List.of(), Map.of("type", "summary")),
        new CardMetadata("30", "test", LocalDateTime.now().minusDays(1), LocalDateTime.now())
    );

    //When
    CardQueryResponse response = parser.parse(lokiResponse, cardRequest);

    //Then
    Assertions.assertThat(response.currentEntries()).hasSize(2);

  }
}

