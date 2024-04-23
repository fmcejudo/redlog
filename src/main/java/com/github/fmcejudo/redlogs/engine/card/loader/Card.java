package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;

import java.util.List;

record Card(String project, String environment, List<CardQueryRequest> cardQueries) {
}

