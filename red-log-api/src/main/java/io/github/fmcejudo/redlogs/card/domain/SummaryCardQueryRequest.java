package io.github.fmcejudo.redlogs.card.domain;

public record SummaryCardQueryRequest(String id,
                               String type,
                               String description,
                               String query,
                               String executionId) implements CardQueryRequest {
    SummaryCardQueryRequest(CardQueryContext ctx) {
        this(ctx.id(), ctx.type(), ctx.description(), ctx.query(), null);
    }
}
