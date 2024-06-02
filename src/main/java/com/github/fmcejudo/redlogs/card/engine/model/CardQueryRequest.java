package com.github.fmcejudo.redlogs.card.engine.model;


import java.time.LocalDate;

public record CardQueryRequest(String applicationName,
                               String id,
                               String description,
                               CardType cardType,
                               String query,
                               LocalDate reportDate) {

    public CardQueryRequest(String applicationName,
                     String id,
                     String description,
                     CardType cardType,
                     String query){
        this(applicationName, id, description, cardType, query, null);
    }

    public CardQueryRequest withReportDate(final LocalDate reportDate) {
        return new CardQueryRequest(applicationName, id, description, cardType, query, reportDate);
    }
}
