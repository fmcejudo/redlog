package io.github.fmcejudo.redlogs.processor.loki;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public record LokiRequest(CardQueryRequest cardQueryRequest, LocalDateTime startTime, LocalDateTime endTime) {

    public String getQuery() {
        return Optional.ofNullable(this.cardQueryRequest)
                .map(CardQueryRequest::query)
                .orElseThrow(() -> new IllegalStateException("it is required the card request contains query"));
    }



}
