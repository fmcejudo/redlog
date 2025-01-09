package io.github.fmcejudo.redlogs.loki;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;

@FunctionalInterface
public interface LokiSummaryCardRequestGenerator {

  LokiSummaryCardRequest generate();

  static LokiSummaryCardRequestGenerator withCardRequestId(String cardRequestId) {
    return () -> {
      CardQuery cardQuery =
          new CardQuery(cardRequestId, "LOKI", "description for " + cardRequestId, Map.of("type", "summary", "query", "{}"));
      CardMetadata cardMetadata = new CardMetadata("30", "test", LocalDateTime.now().minusDays(1), LocalDateTime.now());

      return LokiSummaryCardRequest.from(cardQuery, cardMetadata);
    };
  }

  default LokiSummaryCardRequestGenerator withShowLabels(List<String> labels) {
    return () -> {
      LokiSummaryCardRequest generate = this.generate();
      Map<String, String> properties = Map.of("type", "summary", "query", generate.query(), "showLabels", String.join(",", labels));
      CardQuery cardQuery = new CardQuery(generate().id(), generate.processor(), generate.description(), properties);
      return LokiSummaryCardRequest.from(cardQuery, generate.metadata());
    };
  }
}
