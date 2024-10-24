package com.github.fmcejudo.redlogs.client.loki;

import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public record LokiRequest(CardQueryRequest cardQueryRequest, LocalDateTime startTime, LocalDateTime endTime) {

    public String getQuery() {
        return Optional.ofNullable(this.cardQueryRequest)
                .map(CardQueryRequest::query)
                .orElseThrow(() -> new IllegalStateException("it is required the card request contains query"));
    }



}
