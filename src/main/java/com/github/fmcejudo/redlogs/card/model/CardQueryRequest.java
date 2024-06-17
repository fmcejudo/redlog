package com.github.fmcejudo.redlogs.card.model;


import java.time.LocalDate;
import java.time.LocalTime;

public record CardQueryRequest(String applicationName,
                               String id,
                               String description,
                               CardType cardType,
                               String query,
                               LocalDate reportDate,
                               LocalTime time,
                               String range,
                               String executionId) {

    public CardQueryRequest(String applicationName,
                            String id,
                            String description,
                            CardType cardType,
                            String query,
                            LocalTime time,
                            String range) {
        this(applicationName, id, description, cardType, query, null, time, range, null);
    }

    public CardQueryRequest withReportDate(final LocalDate reportDate) {
        return new CardQueryRequest(applicationName, id, description, cardType, query, reportDate, time, range, executionId);
    }

    public CardQueryRequest withExecutionId(final String executionId) {
        return new CardQueryRequest(applicationName, id, description, cardType, query, reportDate, time, range, executionId);
    }
}
