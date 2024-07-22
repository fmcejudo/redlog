package com.github.fmcejudo.redlogs.card.model;


public record CardQueryRequest(String id,
                               String description,
                               CardType cardType,
                               String query,
                               String executionId) {

    public CardQueryRequest(String id,
                            String description,
                            CardType cardType,
                            String query) {
        this(id, description, cardType, query, null);
    }

    public CardQueryRequest withExecutionId(final String executionId) {
        return new CardQueryRequest(id, description, cardType, query, executionId);
    }
}
