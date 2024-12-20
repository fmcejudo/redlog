package io.github.fmcejudo.redlogs.card.domain;


public sealed interface CardQueryRequest permits CounterCardQueryRequest, SummaryCardQueryRequest {

    String id();

    String description();

    String query();

    String executionId();

    String type();

    public static <T extends CardQueryRequest> CardQueryRequest getInstance(CardType type,
                                                                            CardQueryContext cardQueryContext) {
        return switch (type) {
            case COUNT -> new CounterCardQueryRequest(cardQueryContext);
            case SUMMARY -> new SummaryCardQueryRequest(cardQueryContext);
        };
    }

    default CardQueryRequest withExecutionId(final String executionId) {

        if (this instanceof CounterCardQueryRequest cqr) {
            return new CounterCardQueryRequest(
                    cqr.id(), cqr.type(), cqr.description(), cqr.query(), cqr.expectAtLeast(), executionId
            );
        }
        if (this instanceof SummaryCardQueryRequest sqr) {
            return new SummaryCardQueryRequest(sqr.id(), sqr.type(), sqr.description(), sqr.query(), executionId);
        }
        throw new RuntimeException("type not recognised");
    }

    public record CardQueryContext(String id, String type, String description, String query, Integer expectAtLeast) {

        public CardQueryContext(String id, String type, String description, String query) {
            this(id, type, description, query, 1);
        }
    }
}


