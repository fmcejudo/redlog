package io.github.fmcejudo.redlogs.card.domain;

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

    private final List<CardQueryRequest> cardQueryRequests;

    private final Map<String, String> parameters;

    public CardRequest(final String applicationName, final LocalDate date, final LocalDateTime startTime,
                       final LocalDateTime endTime, final List<CardQueryRequest> cardQueryRequests,
                       final Map<String, String> parameters) {

        this(applicationName, date, startTime, endTime, cardQueryRequests, null, parameters);
    }

    private CardRequest(final String applicationName, final LocalDate date, final LocalDateTime startTime,
                        final LocalDateTime endTime, final List<CardQueryRequest> cardQueryRequests,
                        final String executionId, final Map<String, String> parameters) {

        this.applicationName = applicationName;
        this.cardQueryRequests = cardQueryRequests;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.executionId = executionId;
        this.parameters = parameters;
    }

    public CardRequest withExecutionId(final String executionId) {
        return new CardRequest(applicationName, date, startTime, endTime, cardQueryRequests, executionId, parameters);
    }

    public String applicationName() {
        return applicationName;
    }

    public List<CardQueryRequest> cardQueryRequests() {
        return cardQueryRequests;
    }

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
