package com.github.fmcejudo.redlogs.client.loki;

import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LokiRequest(String query, LocalDate reportDate, LocalTime time, String range) {

    public LokiRequest(final CardQueryRequest cardQueryRequest) {
        this(
                cardQueryRequest.query(),
                cardQueryRequest.reportDate(),
                cardQueryRequest.time(),
                cardQueryRequest.range()
        );
    }

    public LocalDateTime endTime() {
        return LocalDateTime.of(reportDate, time);
    }

    public LocalDateTime startTime() {
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
