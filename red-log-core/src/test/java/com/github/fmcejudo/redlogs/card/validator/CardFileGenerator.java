package com.github.fmcejudo.redlogs.card.validator;

import java.time.LocalTime;
import java.util.List;

import com.github.fmcejudo.redlogs.card.loader.CardFile;

@FunctionalInterface
interface CardFileGenerator {

  CardFile generate();

  static CardFileGenerator createInstance() {
    return () -> new CardFile(List.of(), null, null, List.of());
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

}
