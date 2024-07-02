package com.github.fmcejudo.redlogs.card.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CardRequest {

    private final String applicationName;

    private final LocalDate date;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final String executionId;

    private final List<CardQueryRequest> cardQueryRequests;

    public CardRequest(final String applicationName, final LocalDate date, final LocalDateTime startTime,
                       final LocalDateTime endTime, final List<CardQueryRequest> cardQueryRequests) {

        this(applicationName, date, startTime, endTime, cardQueryRequests, null);
    }

    private CardRequest(final String applicationName, final LocalDate date, final LocalDateTime startTime,
                        final LocalDateTime endTime, final List<CardQueryRequest> cardQueryRequests,
                        final String executionId) {

        this.applicationName = applicationName;
        this.cardQueryRequests = cardQueryRequests;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.executionId = executionId;
    }

    public CardRequest withExecutionId(final String executionId) {
        return new CardRequest(applicationName, date, startTime, endTime, cardQueryRequests, executionId);
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

}
