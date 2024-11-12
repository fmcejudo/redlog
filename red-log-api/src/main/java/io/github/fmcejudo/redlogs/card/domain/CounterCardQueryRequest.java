package io.github.fmcejudo.redlogs.card.domain;

public record CounterCardQueryRequest(String id,
                               String type,
                               String description,
                               String query,
                               Integer expectAtLeast,
                               String executionId) implements CardQueryRequest {

    CounterCardQueryRequest(CardQueryContext ctx) {
        this(ctx.id(), ctx.type(), ctx.description(), ctx.query(), ctx.expectAtLeast(), null);
    }
}
