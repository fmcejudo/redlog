package io.github.fmcejudo.redlogs.loki.processor;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiConnectionDetails;

@FunctionalInterface
public interface LokiCardQueryProcessor extends CardQueryProcessor {

  static CardQueryProcessor withLokiConnectionDetails(final Map<String, String> connectionDetails) {
    LokiConnectionDetails lokiConnectionDetails = LokiConnectionDetails.from(connectionDetails);
    LokiClientFactory lokiClientFactory = LokiClientFactory.createInstance(lokiConnectionDetails);
    CardQueryProcessor countCardProcessor = new CountCardProcessor(lokiClientFactory, lokiConnectionDetails);
    CardQueryProcessor summaryCardProcessor = new SummaryCardProcessor(lokiClientFactory, lokiConnectionDetails);
    return cardQueryRequest -> {
      if (cardQueryRequest instanceof LokiCountCardRequest lokiCountCardRequest) {
        return countCardProcessor.process(lokiCountCardRequest);
      }
      if (cardQueryRequest instanceof LokiSummaryCardRequest lokiSummaryCardRequest) {
        return summaryCardProcessor.process(lokiSummaryCardRequest);
      }
      throw new IllegalArgumentException("card request is not recognized by this plugin");
    };
  }
}

