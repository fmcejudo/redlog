package com.github.fmcejudo.redlogs.engine.card.converter;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;

import java.util.List;

@FunctionalInterface
public interface CardConverter {

    List<CardQueryRequest> convert(final String content, final String application);
}
