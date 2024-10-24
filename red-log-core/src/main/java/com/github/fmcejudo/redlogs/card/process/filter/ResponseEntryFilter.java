package com.github.fmcejudo.redlogs.card.process.filter;

import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.card.model.CounterCardQueryRequest;

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


