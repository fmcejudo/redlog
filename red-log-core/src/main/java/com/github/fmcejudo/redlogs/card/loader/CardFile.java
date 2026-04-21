package com.github.fmcejudo.redlogs.card.loader;

import java.time.LocalTime;
import java.util.List;

import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import io.github.fmcejudo.redlogs.card.CardQuery;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record CardFile(List<String> parameters,
                       LocalTime time,
                       String range,
                       List<CardQuery> queries) {

  public CardFile {
    if (parameters == null || parameters.isEmpty()) {
      parameters = List.of();
    } else {
      parameters = List.copyOf(parameters);
    }

    if (queries == null || queries.isEmpty()) {
      throw new CardExecutionException("it can't run a report template with no queries");
    }
  }

}

