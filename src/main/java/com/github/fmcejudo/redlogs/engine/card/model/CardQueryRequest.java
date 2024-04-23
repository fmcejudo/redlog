package com.github.fmcejudo.redlogs.engine.card.model;


public record CardQueryRequest(String applicationName, String id, String description, CardType cardType, String query) {
}
