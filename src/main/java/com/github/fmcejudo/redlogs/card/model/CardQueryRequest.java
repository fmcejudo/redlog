package com.github.fmcejudo.redlogs.card.model;


import java.time.LocalDate;

public record CardQueryRequest(String applicationName,
                               String id,
                               String description,
                               CardType cardType,
                               String query,
                               LocalDate reportDate,
                               String executionId) {

    public CardQueryRequest(String applicationName,
                     String id,
                     String description,
                     CardType cardType,
                     String query){
        this(applicationName, id, description, cardType, query, null,null);
    }

    public CardQueryRequest withReportDate(final LocalDate reportDate) {
        return new CardQueryRequest(applicationName, id, description, cardType, query, reportDate, executionId);
    }

    public CardQueryRequest withExecutionId(final String executionId) {
        return new CardQueryRequest(applicationName, id, description, cardType, query, reportDate, executionId);
    }
}
