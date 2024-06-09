package com.github.fmcejudo.redlogs.card.process;

import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;

@FunctionalInterface
public interface CardProcessor {

    CardQueryResponse process(CardQueryRequest cardQuery);
}
