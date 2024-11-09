package io.github.fmcejudo.redlogs.card.domain;

public record SummaryCardQueryRequest(String id,
                               String description,
                               String query,
                               String executionId) implements CardQueryRequest {
    SummaryCardQueryRequest(CardQueryContext ctx) {
        this(ctx.id(), ctx.description(), ctx.query(), null);
    }
}
