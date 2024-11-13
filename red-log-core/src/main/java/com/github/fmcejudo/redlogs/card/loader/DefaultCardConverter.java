package com.github.fmcejudo.redlogs.card.loader;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardException;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest.CardQueryContext;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import org.apache.commons.text.StringSubstitutor;

final class DefaultCardConverter implements CardConverter {

    private final ObjectMapper mapper;

    private final CardValidator cardValidator;

    public DefaultCardConverter() {
        this.mapper = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule());
        this.cardValidator = CardValidator.validate(new ParameterValidator())
                .thenValidate(new RangeValidator())
                .thenValidate(new TimeValidator());
    }

    public CardRequest convert(final String content, final CardContext cardContext) {
        try {
            CardFile cardFile = mapper.readValue(content, CardFile.class);
            validateCards(cardFile, cardContext);
            List<CardQueryRequest> cardQueryRequests = readQueries(cardContext, cardFile);
            LocalTime time = cardFile.time();
            String range = findRange(cardFile.range(), cardContext);
            TimeBoundaries tb = new TimeBoundaries(cardContext.reportDate(), time, range);
            return new CardRequest(
                    cardContext.applicationName(), cardContext.reportDate(),
                    tb.startTime(), tb.endTime(), cardQueryRequests, cardContext.parameters()
            );
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

    private List<CardQueryRequest> readQueries(final CardContext cardContext, final CardFile cardFile) {
        return cardFile.queries().stream().map(convertToQueryRequest(cardFile, cardContext)).toList();
    }


    private Function<CardQuery, CardQueryRequest> convertToQueryRequest(
            final CardFile cardFile, final CardContext cardContext) {

        UnaryOperator<String> queryReplaceFn = buildResolvedQuery(cardFile, cardContext);

        return q -> {
            CardQueryContext context = new CardQueryContext(
                    q.id(), q.source(), q.description(), queryReplaceFn.apply(q.query()), ofNullable(q.expectedAtLeast()).orElse(1)
            );
            return CardQueryRequest.getInstance(q.type(), context);
        };
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

    private record TimeBoundaries(LocalDate date, LocalTime time, String range) {


        LocalDateTime endTime() {
            return LocalDateTime.of(date, time);
        }

        LocalDateTime startTime() {
            String stringAmount = range.substring(0, range.length() - 1);
            int amount = Integer.parseInt(stringAmount);
            if (range.endsWith("m")) {
                return endTime().minusMinutes(amount);
            } else if (range.endsWith("h")) {
                return endTime().minusHours(amount);
            }
            throw new IllegalStateException("wrong range expression");
        }
    }

}

