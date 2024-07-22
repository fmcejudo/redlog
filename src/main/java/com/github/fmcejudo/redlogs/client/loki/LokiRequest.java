package com.github.fmcejudo.redlogs.client.loki;

import java.time.LocalDateTime;

public record LokiRequest(String query, LocalDateTime startTime, LocalDateTime endTime) {

}
