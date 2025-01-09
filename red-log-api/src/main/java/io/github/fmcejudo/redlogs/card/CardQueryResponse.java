package io.github.fmcejudo.redlogs.card;

import java.time.LocalDate;
import java.util.List;

public record CardQueryResponse(
        LocalDate date,
        String id,
        String executionId,
        String description,
        List<CardQueryResponseEntry> currentEntries,
        String link,
        String error) {


    public static CardQueryResponse success(
            LocalDate date,
            String id,
            String executionId,
            String description,
            String link,
            List<CardQueryResponseEntry> entries) {

        return new CardQueryResponse(
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
            LocalDate date,
            String id,
            String executionId,
            String description,
            String error) {

        return new CardQueryResponse(
                date,
                id,
                executionId,
                description,
                List.of(),
                null,
                error);
    }

}
