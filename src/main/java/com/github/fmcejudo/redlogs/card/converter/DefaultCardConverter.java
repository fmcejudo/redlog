package com.github.fmcejudo.redlogs.card.converter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.CardType;
import org.apache.commons.text.StringSubstitutor;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

final class DefaultCardConverter implements CardConverter {

    private final ObjectMapper mapper;

    public DefaultCardConverter() {
        this.mapper = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule());
    }

    public List<CardQueryRequest> convert(final String content, final CardContext cardContext) {
        try {
            CardFile cardFile = mapper.readValue(content, CardFile.class);
            validateParameters(cardFile, cardContext.parameters());
            return cardFile.queries().stream().map(convertToQueryRequest(cardFile, cardContext)).toList();
        } catch (Exception e) {
            throw new CardExecutionException(e.getMessage());
        }
    }

    private void validateParameters(final CardFile cardFile, final Map<String, String> parameters) {
        if (cardFile.parameters == null || cardFile.parameters.isEmpty()) {
            return;
        }
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
            String query = q;
            if (cardFile.range() != null) {
                query = query.replace("_RANGE_", cardFile.range());
            }
            StringSubstitutor substitutor = new StringSubstitutor(cardContext.parameters(), "<", ">");
            return substitutor.replace(query.replace("<common_query>", cardFile.commonQuery()));
        };

        return q -> new CardQueryRequest(
                cardContext.applicationName(), q.id(), q.description(), q.type(), queryReplaceFn.apply(q.query));
    }

    @JsonSerialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    record CardFile(@JsonProperty("common_query") String commonQuery,
                    List<String> parameters,
                    LocalTime time,
                    String range,
                    List<CardQuery> queries) {

    }

    @JsonSerialize
    record CardQuery(String id, String description, CardType type, String query) {

    }
}
