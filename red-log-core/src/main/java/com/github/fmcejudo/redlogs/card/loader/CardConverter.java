package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;

@FunctionalInterface
public interface CardConverter {

    CardRequest convert(final String content, final CardContext cardContext);
}
