package com.github.fmcejudo.redlogs.engine.card.process;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;

import java.time.LocalDate;

@FunctionalInterface
public interface CardProcessor {

    CardQueryResponse process(CardQueryRequest cardQuery, LocalDate reportDate);
}
