package io.github.fmcejudo.redlogs.loki.converter;

import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;

@FunctionalInterface
public interface LokiCardQueryConverter extends CardQueryConverter {

  static LokiCardQueryConverter createInstance() {
    return LokiCountCardRequest::from;
  }
}
