package com.github.fmcejudo.redlogs.card.engine.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.engine.model.CardType;

import java.util.List;
import java.util.function.Function;

final class DefaultCardConverter implements CardConverter {

    private final ObjectMapper mapper;

    public DefaultCardConverter() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public List<CardQueryRequest> convert(final String content, final String applicationName) {
        try {
            CardFile cardFile = mapper.readValue(content, CardFile.class);
            return cardFile.queries().stream().map(convertToQueryRequest(cardFile, applicationName)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Function<CardQuery, CardQueryRequest> convertToQueryRequest(final CardFile cardFile, final String appName) {
        return q -> new CardQueryRequest(
                appName, q.id(), q.description(), q.type(), q.query.replace("<common_query>", cardFile.commonQuery())
        );
    }

    @JsonSerialize
    record CardFile(@JsonProperty("common_query") String commonQuery, List<CardQuery> queries) {

    }

    @JsonSerialize
    record CardQuery(String id, String description, CardType type, String query) {

    }
}
