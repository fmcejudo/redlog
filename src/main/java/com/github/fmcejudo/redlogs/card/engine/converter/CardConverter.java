package com.github.fmcejudo.redlogs.card.engine.converter;

import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;

import java.util.List;

@FunctionalInterface
public interface CardConverter {

    List<CardQueryRequest> convert(final String content, final String application);
}
