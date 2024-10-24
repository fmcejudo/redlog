package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.model.CardRequest;

@FunctionalInterface
public interface CardLoader {

    public abstract CardRequest load(CardContext cardExecutionContext);

}
