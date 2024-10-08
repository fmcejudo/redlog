package com.github.fmcejudo.redlogs.card.loader;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
interface CardFileGenerator {

    CardFile generate();

    static CardFileGenerator createInstance() {
        return () -> new CardFile(null, List.of(), null, null, List.of());
    }

    default CardFileGenerator withCommonQuery(final String commonQuery) {
        return () -> {
            CardFile cardFile = this.generate();
            return new CardFile(
                    commonQuery, cardFile.parameters(), cardFile.time(), cardFile.range(), cardFile.queries()
            );
        };
    }

    default CardFileGenerator withParameters(List<String> parametersNames) {
        return () -> {
            CardFile cardFile = this.generate();
            return new CardFile(
                    cardFile.commonQuery(), parametersNames, cardFile.time(), cardFile.range(), cardFile.queries()
            );
        };
    }

    default CardFileGenerator withTime(LocalTime time) {
        return () -> {
            CardFile cardFile = this.generate();
            return new CardFile(
                    cardFile.commonQuery(), cardFile.parameters(), time, cardFile.range(), cardFile.queries()
            );
        };
    }

    default CardFileGenerator withRange(final String range) {
        return () -> {
          CardFile cardFile = this.generate();
            return new CardFile(
                    cardFile.commonQuery(), cardFile.parameters(), cardFile.time(), range, cardFile.queries()
            );
        };
    }

    default CardFileGenerator addQuery(final CardQuery cardQuery) {
        return () -> {
            CardFile cardFile = this.generate();
            List<CardQuery> cardQueries = new ArrayList<>(cardFile.queries());
            cardQueries.add(cardQuery);
            return new CardFile(
                    cardFile.commonQuery(), cardFile.parameters(), cardFile.time(), cardFile.range(), cardQueries
            );
        };
    }
}
