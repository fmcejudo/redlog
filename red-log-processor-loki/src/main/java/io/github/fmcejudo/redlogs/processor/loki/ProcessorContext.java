package io.github.fmcejudo.redlogs.processor.loki;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

record ProcessorContext(CardRequest cardRequest, CardQueryRequest cardQueryRequest) {

    String applicationName() {
        return cardRequest().applicationName();
    }

    String executionId() {
        return cardRequest.executionId();
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
