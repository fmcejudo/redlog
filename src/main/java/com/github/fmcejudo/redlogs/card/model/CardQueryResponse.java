package com.github.fmcejudo.redlogs.card.model;

import java.time.LocalDate;
import java.util.List;

public record CardQueryResponse(
        String applicationName,
        LocalDate date,
        String id,
        String executionId,
        String description,
        List<CardQueryResponseEntry> currentEntries,
        String link,
        String error) {


    public static CardQueryResponse success(
            String applicationName,
            LocalDate date,
            String id,
            String executionId,
            String description,
            String link,
            List<CardQueryResponseEntry> entries) {

        return new CardQueryResponse(
                applicationName,
                date,
                id,
                executionId,
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
            String executionId,
            String description,
            String error) {

        return new CardQueryResponse(
                applicationName,
                date,
                id,
                executionId,
                description,
                List.of(),
                null,
                error);
    }

}
