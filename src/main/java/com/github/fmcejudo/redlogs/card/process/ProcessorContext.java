package com.github.fmcejudo.redlogs.card.process;

import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.card.model.CardType;

import java.time.LocalDate;
import java.time.LocalDateTime;

record ProcessorContext(CardRequest cardRequest, CardQueryRequest cardQueryRequest) {

    String applicationName() {
        return cardRequest().applicationName();
    }

    String executionId() {
        return cardRequest.executionId();
    }

    CardType type() {
        return cardQueryRequest.cardType();
    }

    String id() {
        return cardQueryRequest.id();
    }

    String description() {
        return cardQueryRequest.description();
    }

    String query() {
        return cardQueryRequest.query();
    }

    LocalDateTime start() {
        return cardRequest.startTime();
    }

    LocalDateTime end() {
        return cardRequest.endTime();
    }

    LocalDate reportDate() {
        return end().toLocalDate();
    }
}
