package io.github.fmcejudo.redlogs.loki.processor.connection;

import java.time.LocalDateTime;

public record LokiRequest(String query, LocalDateTime startTime, LocalDateTime endTime) {

}
