package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;

import java.util.List;

record Card(String project, String environment, List<CardQueryRequest> cardQueries) {
}

