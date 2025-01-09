package io.github.fmcejudo.redlogs.loki.converter;

import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;

@FunctionalInterface
public interface LokiCardQueryConverter extends CardQueryConverter {

  static LokiCardQueryConverter createInstance() {

    return (cardQuery, cardMetadata) -> {

      String type = cardQuery.properties().get("type");
      if ("count".equalsIgnoreCase(type)) {
        return LokiCountCardRequest.from(cardQuery, cardMetadata);
      } else if ("summary".equalsIgnoreCase(type)) {
        return LokiSummaryCardRequest.from(cardQuery, cardMetadata);
      }
      throw new IllegalStateException("Type " + type + " is not valid for LOKI processor");
    };
  }
}
