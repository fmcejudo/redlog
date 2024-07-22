package com.github.fmcejudo.redlogs.card.converter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.fmcejudo.redlogs.card.model.CardType;

@JsonSerialize
record CardQuery(String id, String description, CardType type, String query) {

}
