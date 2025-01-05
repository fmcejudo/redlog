package io.github.fmcejudo.redlogs.loki.processor;

import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.loki.LokiSummaryCardRequestGenerator;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SummaryCardResponseParserTest {

  LokiResponseGenerator lokiResponseGenerator;

  @BeforeEach
  void setUp() {
    this.lokiResponseGenerator =
        LokiResponseGenerator.status(true).addResult(Map.of("message", "your message", "class", "i.g.f.r.l.p.Test", "time", "now"), 1L);
  }

  @Test
  void shouldNotFilterLabels() {
    //Given
    SummaryCardResponseParser parser = (SummaryCardResponseParser) LokiCardResponseParser.createParser(LokiSummaryCardRequest.class);
    LokiResponse response =lokiResponseGenerator.generate();
    LokiSummaryCardRequest cardRequest = LokiSummaryCardRequestGenerator.withCardRequestId("summary-request").generate();
    //When
    CardQueryResponse cardResponse = parser.parse(response, cardRequest);

    //Then
    Assertions.assertThat(cardResponse.currentEntries()).allSatisfy(c -> {
      Assertions.assertThat(c.labels()).containsOnlyKeys("message", "class", "time");
    });
  }

  @Test
  void shouldFilterLabels() {
    //Given
    SummaryCardResponseParser parser = (SummaryCardResponseParser) LokiCardResponseParser.createParser(LokiSummaryCardRequest.class);
    LokiResponse response =lokiResponseGenerator.generate();
    LokiSummaryCardRequest cardRequest = LokiSummaryCardRequestGenerator
        .withCardRequestId("summary-request")
        .withShowLabels(List.of("time", "message"))
        .generate();

    //When
    CardQueryResponse cardResponse = parser.parse(response, cardRequest);

    //Then
    Assertions.assertThat(cardResponse.currentEntries()).allSatisfy(c -> {
      Assertions.assertThat(c.labels()).containsOnlyKeys("message", "time");
    });
  }

  @Test
  void shouldTrimFilterLabels() {
    //Given
    SummaryCardResponseParser parser = (SummaryCardResponseParser) LokiCardResponseParser.createParser(LokiSummaryCardRequest.class);
    LokiResponse response =lokiResponseGenerator.generate();
    LokiSummaryCardRequest cardRequest = LokiSummaryCardRequestGenerator
        .withCardRequestId("summary-request")
        .withShowLabels(List.of("time ", " message "))
        .generate();

    //When
    CardQueryResponse cardResponse = parser.parse(response, cardRequest);

    //Then
    Assertions.assertThat(cardResponse.currentEntries()).allSatisfy(c -> {
      Assertions.assertThat(c.labels()).containsOnlyKeys("message", "time");
    });
  }

}