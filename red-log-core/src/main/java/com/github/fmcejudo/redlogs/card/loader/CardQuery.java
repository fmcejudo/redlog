package com.github.fmcejudo.redlogs.card.loader;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.fmcejudo.redlogs.card.domain.CardType;

@JsonSerialize
record CardQuery(String id, String description, CardType type, String query, Integer expectedAtLeast) {

    public CardQuery(String id, String description, CardType type, String query) {
        this(id, description, type, query, 1);
    }

}
