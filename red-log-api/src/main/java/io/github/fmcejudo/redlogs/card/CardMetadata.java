package io.github.fmcejudo.redlogs.card;

import java.time.LocalDateTime;

public record CardMetadata(String executionId, String applicationName, LocalDateTime startTime, LocalDateTime endTime) {

}
