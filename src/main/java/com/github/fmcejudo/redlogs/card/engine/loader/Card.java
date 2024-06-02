package com.github.fmcejudo.redlogs.card.engine.loader;

import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;

import java.util.List;

record Card(String project, String environment, List<CardQueryRequest> cardQueries) {
}

