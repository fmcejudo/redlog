package com.github.fmcejudo.redlogs.card.converter;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.model.CardRequest;

@FunctionalInterface
public interface CardConverter {

    CardRequest convert(final String content, final CardContext cardContext);
}
