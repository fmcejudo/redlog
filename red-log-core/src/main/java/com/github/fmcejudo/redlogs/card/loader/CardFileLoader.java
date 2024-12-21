package com.github.fmcejudo.redlogs.card.loader;

import java.util.function.Function;

import com.github.fmcejudo.redlogs.card.CardContext;

@FunctionalInterface
public interface CardFileLoader extends Function<CardContext, CardFile> {

    @Override
    default CardFile apply(CardContext cardContext) {
        return this.load(cardContext);
    }

    public abstract CardFile load(CardContext cardExecutionContext);

}
