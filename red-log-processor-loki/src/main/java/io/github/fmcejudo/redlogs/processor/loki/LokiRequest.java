package io.github.fmcejudo.redlogs.processor.loki;

import java.time.LocalDateTime;
import java.util.Optional;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;

public record LokiRequest(CardQueryRequest cardQueryRequest, LocalDateTime startTime, LocalDateTime endTime) {

    public String getQuery() {
        return Optional.ofNullable(this.cardQueryRequest)
                .map(CardQueryRequest::query)
                .orElseThrow(() -> new IllegalStateException("it is required the card request contains query"));
    }



}
