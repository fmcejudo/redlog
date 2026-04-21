package com.github.fmcejudo.redlogs.card.validator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardQuery;

@FunctionalInterface
interface CardFileGenerator {

  CardFile generate();

  static CardFileGenerator withCardQuery(CardQuery cardQuery) {

    return () -> new CardFile(List.of(), null, null, List.of(cardQuery));
  }

  default CardFileGenerator withParameters(List<String> parametersNames) {
    return () -> {
      CardFile cardFile = this.generate();
      return new CardFile(parametersNames, cardFile.time(), cardFile.range(), cardFile.queries());
    };
  }

  default CardFileGenerator withTime(LocalTime time) {
    return () -> {
      CardFile cardFile = this.generate();
      return new CardFile(cardFile.parameters(), time, cardFile.range(), cardFile.queries());
    };
  }

  default CardFileGenerator withRange(final String range) {
    return () -> {
      CardFile cardFile = this.generate();
      return new CardFile(cardFile.parameters(), cardFile.time(), range, cardFile.queries());
    };
  }

  default CardFileGenerator addLokiQuery(String id, String description, List<String> tags, Map<String, String> properties) {
    return () -> {
      CardFile cf = this.generate();

      List<CardQuery> newQueries = new ArrayList<>(cf.queries());
      CardQuery cardQuery = new CardQuery(id, "LOKI", description, tags, properties);
      newQueries.add(cardQuery);

      return new CardFile(cf.parameters(), cf.time(), cf.range(), List.copyOf(newQueries));
    };
  }

}
