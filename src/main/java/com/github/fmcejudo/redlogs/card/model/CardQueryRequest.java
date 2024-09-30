package com.github.fmcejudo.redlogs.card.model;


public sealed interface CardQueryRequest permits CounterCardQueryRequest, SummaryCardQueryRequest {

    String id();

    String description();

    String query();

    String executionId();

    public static <T extends CardQueryRequest> CardQueryRequest getInstance(CardType type,
                                                                            CardQueryContext cardQueryContext) {
        return switch (type) {
            case COUNT -> new CounterCardQueryRequest(cardQueryContext);
            case SUMMARY -> new SummaryCardQueryRequest(cardQueryContext);
        };
    }

    default CardQueryRequest withExecutionId(final String executionId) {

        if (this instanceof CounterCardQueryRequest cqr) {
            return new CounterCardQueryRequest(cqr.id(), cqr.description(), cqr.query(), executionId);
        }
        if (this instanceof SummaryCardQueryRequest sqr) {
            return new SummaryCardQueryRequest(sqr.id(), sqr.description(), sqr.query(), executionId);
        }
        throw new RuntimeException("type not recognised");
    }

    public record CardQueryContext(String id, String description, String query) {

    }
}


