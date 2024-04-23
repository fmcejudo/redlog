package com.github.fmcejudo.redlogs.engine.card.model;

import java.time.LocalDateTime;
import java.util.List;

public record CardQueryResponse(
        String applicationName,
        LocalDateTime dateTime,
        String id,
        String description,
        List<CardQueryResponseEntry> currentEntries,
        List<CardQueryResponseEntry> previousEntries,
        String link,
        String error) {


    public static CardQueryResponse success(
            String applicationName,
            String id,
            String description,
            String link,
            List<CardQueryResponseEntry> entries) {

        return new CardQueryResponse(
                applicationName,
                LocalDateTime.now().withMinute(0).withSecond(0).withNano(0),
                String.join(".", applicationName, id),
                description,
                entries,
                List.of(),
                link,
                null
        );
    }

    public static CardQueryResponse failure(
            String applicationName,
            String id,
            String description,
            String error) {

        return new CardQueryResponse(applicationName, LocalDateTime.now().withMinute(0).withSecond(0).withNano(0),
                String.join(".", applicationName, id),
                description,
                List.of(),
                List.of(),
                null,
                error);
    }

    public CardQueryResponse addPreviousEntries(List<CardQueryResponseEntry> previousEntries) {
        if (previousEntries.isEmpty()) {
            return this;
        }
        return new CardQueryResponse(
                applicationName,
                LocalDateTime.now().withMinute(0).withSecond(0).withNano(0),
                id,
                description,
                currentEntries,
                previousEntries,
                link,
                null);
    }
}
