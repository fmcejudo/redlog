package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.engine.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;

import java.util.List;

@FunctionalInterface
public interface CardLoader {

    public abstract List<CardQueryRequest> load(String application, CardConverter converter);

}
