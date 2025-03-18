package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory.QueryTypeEnum;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiConnectionDetails;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.range.QueryRangeClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SummaryCardProcessorTest {

  private CardQueryProcessor cardQueryProcessor;

  @BeforeEach
  void setUp() {
    LokiConnectionDetails lokiConnectionDetails = LokiConnectionDetails.from(Map.of("url", "http://loki.sample"));
    LokiClientFactory lokiClientFactory = Mockito.mock(LokiClientFactory.class);
    QueryRangeClient queryRangeClient = Mockito.mock(QueryRangeClient.class);
    Mockito.when(lokiClientFactory.get(QueryTypeEnum.RANGE)).thenReturn(queryRangeClient);
    Mockito.when(queryRangeClient.query(Mockito.any(LokiRequest.class))).thenReturn(new TestLokiResponse());
    this.cardQueryProcessor = new SummaryCardProcessor(lokiClientFactory, lokiConnectionDetails);
  }

  @Test
  void shouldNotProcessCountQueryRequest() {
    //Give
    CardQuery cardQuery = new CardQuery("count-request", "LOKI", "description", List.of(), Map.of(
        "type", "COUNT"
    ));
    CardMetadata cardMetadata = new CardMetadata("20", "test", LocalDateTime.now().minusDays(1), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = LokiCountCardRequest.from(cardQuery, cardMetadata);

    //When && Then
    Assertions.assertThatThrownBy(() -> cardQueryProcessor.process(cardQueryRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("This processor is for summary card requests");
  }

  @Test
  void shouldProcessSummaryQueryRequest() {
    //Give
    CardQuery cardQuery = new CardQuery("summary-request", "LOKI", "description", List.of(), Map.of(
        "type", "SUMMARY", "query", "{}"
    ));
    CardMetadata cardMetadata = new CardMetadata("20", "test", LocalDateTime.now().minusDays(1), LocalDateTime.now());
    CardQueryRequest cardQueryRequest = LokiSummaryCardRequest.from(cardQuery, cardMetadata);

    //When
    CardQueryResponse response = cardQueryProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(response.id()).isEqualTo("summary-request");
    Assertions.assertThat(response.currentEntries()).isEmpty();
  }

}