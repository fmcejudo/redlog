package io.github.fmcejudo.redlogs.card;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardRequest {

    private final String applicationName;

    private final LocalDate date;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final String executionId;

    private final List<CardQuery> cardQueries;

    private final Map<String, String> parameters;

    public CardRequest(final String applicationName, final LocalDate date, final LocalDateTime startTime,
                       final LocalDateTime endTime, final List<CardQuery> cardQueries,
                       final Map<String, String> parameters) {

        this(applicationName, date, startTime, endTime, cardQueries, null, parameters);
    }

    private CardRequest(final String applicationName, final LocalDate date, final LocalDateTime startTime,
                        final LocalDateTime endTime, final List<CardQuery> cardQueries,
                        final String executionId, final Map<String, String> parameters) {

        this.applicationName = applicationName;
        this.cardQueries = cardQueries;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.executionId = executionId;
        this.parameters = parameters;
    }

    public CardRequest withExecutionId(final String executionId) {
        return new CardRequest(applicationName, date, startTime, endTime, cardQueries, executionId, parameters);
    }

    public String applicationName() {
        return applicationName;
    }

    public List<CardQuery> cardQueries() { return cardQueries; }

    public LocalDate date() {
        return date;
    }

    public LocalDateTime endTime() {
        return endTime;
    }

    public LocalDateTime startTime() {
        return startTime;
    }

    public String executionId() {
        return executionId;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

}
