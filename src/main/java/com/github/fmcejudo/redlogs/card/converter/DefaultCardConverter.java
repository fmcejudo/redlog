package com.github.fmcejudo.redlogs.card.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardException;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import org.apache.commons.text.StringSubstitutor;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

final class DefaultCardConverter implements CardConverter {

    private final ObjectMapper mapper;

    private final CardValidator cardValidator;

    public DefaultCardConverter() {
        this.mapper = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule());
        this.cardValidator = CardValidator.validate(new ParameterValidator())
                .thenValidate(new RangeValidator())
                .thenValidate(new TimeValidator());
    }

    public List<CardQueryRequest> convert(final String content, final CardContext cardContext) {
        try {
            CardFile cardFile = mapper.readValue(content, CardFile.class);
            validateCards(cardFile, cardContext);
            return cardFile.queries().stream().map(convertToQueryRequest(cardFile, cardContext)).toList();
        } catch (Exception e) {
            throw new CardExecutionException(e.getMessage());
        }
    }

    private void validateCards(final CardFile cardFile, final CardContext cardContext) {
        CardValidation cardValidation = cardValidator.validateOn(cardFile, cardContext);
        if (cardValidation.isFailure()) {
            throw new CardException(String.join(";", cardValidation.errors()));
        }
    }


    private Function<CardQuery, CardQueryRequest> convertToQueryRequest(
            final CardFile cardFile, final CardContext cardContext) {

        UnaryOperator<String> queryReplaceFn = buildResolvedQuery(cardFile, cardContext);
        LocalTime time = cardFile.time();
        String range = findRange(cardFile.range(), cardContext);

        return q -> new CardQueryRequest(
                cardContext.applicationName(), q.id(), q.description(), q.type(),
                queryReplaceFn.apply(q.query()), time, range
        );
    }

    private UnaryOperator<String> buildResolvedQuery(final CardFile cardFile, final CardContext cardContext) {
        return q -> {
            String query = q;
            if (cardFile.range() != null) {
                query = query.replace("_RANGE_", cardFile.range());
            }
            StringSubstitutor substitutor = new StringSubstitutor(cardContext.parameters(), "<", ">");
            return substitutor.replace(query.replace("<common_query>", cardFile.commonQuery()));
        };
    }

    private String findRange(final String range, final CardContext cardContext) {
        if (range.startsWith("<") && range.endsWith(">")) {
            return cardContext.parameters().get(range.substring(1, range.length() - 1));
        }
        return range;
    }

}

