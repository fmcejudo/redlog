package com.github.fmcejudo.redlogs.card.engine.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.engine.model.CardType;
import org.apache.commons.text.StringSubstitutor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

final class DefaultCardConverter implements CardConverter {

    private final ObjectMapper mapper;

    public DefaultCardConverter() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    public List<CardQueryRequest> convert(final String content, final CardContext cardContext) {
        try {
            CardFile cardFile = mapper.readValue(content, CardFile.class);
            validateParameters(cardFile, cardContext.parameters());
            return cardFile.queries().stream().map(convertToQueryRequest(cardFile, cardContext)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateParameters(final CardFile cardFile, final Map<String, String> parameters) {
        List<String> unknownParams = cardFile.parameters().stream()
                .filter(Predicate.not(parameters::containsKey))
                .toList();

        if (!unknownParams.isEmpty()) {
            throw new IllegalArgumentException("parameters '%s' not found in parameter map".formatted(unknownParams));
        }
    }

    private Function<CardQuery, CardQueryRequest> convertToQueryRequest(
            final CardFile cardFile, final CardContext cardContext) {

        UnaryOperator<String> queryReplaceFn = q -> {
            StringSubstitutor substitutor = new StringSubstitutor(cardContext.parameters(), "<", ">");
            return substitutor.replace(q.replace("<common_query>", cardFile.commonQuery()));
        };

        return q -> new CardQueryRequest(
                cardContext.applicationName(), q.id(), q.description(), q.type(), queryReplaceFn.apply(q.query));
    }

    @JsonSerialize
    record CardFile(@JsonProperty("common_query") String commonQuery,
                    List<String> parameters,
                    List<CardQuery> queries) {

    }

    @JsonSerialize
    record CardQuery(String id, String description, CardType type, String query) {

    }
}
