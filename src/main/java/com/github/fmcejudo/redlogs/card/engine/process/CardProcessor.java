package com.github.fmcejudo.redlogs.card.engine.process;

import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponse;

@FunctionalInterface
public interface CardProcessor {

    CardQueryResponse process(CardQueryRequest cardQuery);
}
