package com.github.fmcejudo.redlogs.card.model;

public record CounterCardQueryRequest(String id,
                               String description,
                               String query,
                               String executionId) implements CardQueryRequest {

    CounterCardQueryRequest(CardQueryContext ctx) {
        this(ctx.id(), ctx.description(), ctx.query(), null);
    }
}
