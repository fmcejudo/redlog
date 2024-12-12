package io.github.fmcejudo.redlogs.card.processor.filter;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.domain.CounterCardQueryRequest;

@FunctionalInterface
public interface ResponseEntryFilter {

    public static ResponseEntryFilter getInstance(CardQueryRequest cardQueryRequest) {
        if (cardQueryRequest instanceof CounterCardQueryRequest cqr && cqr.expectAtLeast() != null) {
            return r -> r.count() >= cqr.expectAtLeast();
        }
        return r -> true;
    }

    boolean filter(CardQueryResponseEntry cardQueryResponseEntry);
}


