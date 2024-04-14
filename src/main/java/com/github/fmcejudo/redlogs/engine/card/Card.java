package com.github.fmcejudo.redlogs.engine.card;

import java.util.List;

record Card(String project, String environment, List<CardQuery> cardQueries) {
}

record CardQuery(String id, String description, CardType cardType, String query, String tags) {
}

enum CardType {
    SERVICE, COUNT
}
