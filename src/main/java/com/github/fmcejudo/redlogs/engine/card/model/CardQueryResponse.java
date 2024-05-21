package com.github.fmcejudo.redlogs.engine.card.model;

import java.time.LocalDate;
import java.util.List;

public record CardQueryResponse(
        String applicationName,
        LocalDate date,
        String id,
        String description,
        List<CardQueryResponseEntry> currentEntries,
        String link,
        String error) {


    public static CardQueryResponse success(
            String applicationName,
            LocalDate date,
            String id,
            String description,
            String link,
            List<CardQueryResponseEntry> entries) {

        return new CardQueryResponse(
                applicationName,
                date,
                id,
                description,
                entries,
                link,
                null
        );
    }

    public static CardQueryResponse failure(
            String applicationName,
            LocalDate date,
            String id,
            String description,
            String error) {

        return new CardQueryResponse(
                applicationName,
                date,
                id,
                description,
                List.of(),
                null,
                error);
    }

}
