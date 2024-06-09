package com.github.fmcejudo.redlogs.card.converter;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;

import java.util.List;

@FunctionalInterface
public interface CardConverter {

    List<CardQueryRequest> convert(final String content, final CardContext cardContext);
}
