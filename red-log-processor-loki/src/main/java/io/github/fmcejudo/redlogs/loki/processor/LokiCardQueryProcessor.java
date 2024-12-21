package io.github.fmcejudo.redlogs.loki.processor;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiConnectionDetails;

@FunctionalInterface
public interface LokiCardQueryProcessor extends CardQueryProcessor {

  static CardQueryProcessor withLokiConnectionDetails(final Map<String, String> connectionDetails) {
    LokiClientFactory lokiClientFactory = LokiClientFactory.createInstance(LokiConnectionDetails.from(connectionDetails));
    CountCardProcessor countCardProcessor = new CountCardProcessor(lokiClientFactory);
    return cardQueryRequest -> {
      if (cardQueryRequest instanceof LokiCountCardRequest lokiCountCardRequest) {
        return countCardProcessor.process(lokiCountCardRequest);
      }
      throw new IllegalArgumentException("card request is not recognized by this plugin");
    };
  }
}

