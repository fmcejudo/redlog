package com.github.fmcejudo.redlogs.card.engine.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;

import java.util.List;

@FunctionalInterface
public interface CardLoader {

    public abstract List<CardQueryRequest> load(CardContext cardExecutionContext);

}
