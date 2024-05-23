package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;

import java.time.LocalDate;
import java.util.List;

@FunctionalInterface
public interface CardLoader {

    public abstract List<CardQueryRequest> load(String application, LocalDate reportDate);

}
