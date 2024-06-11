package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;

import java.util.List;

@FunctionalInterface
public interface CardLoader {

    public abstract List<CardQueryRequest> load(CardContext cardExecutionContext);

}
