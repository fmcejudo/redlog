package io.github.fmcejudo.redlogs.card.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;

public record ProcessorContext(String executionId, CardQueryRequest cardQueryRequest, LocalDateTime startTime, LocalDateTime endTime) {

  public String id() {
    return cardQueryRequest.id();
  }

  public String description() {
    return cardQueryRequest.description();
  }

  public String query() {
    return cardQueryRequest.query();
  }

  public LocalDateTime start() {
    return startTime;
  }

  public LocalDateTime end() {
    return endTime;
  }

  public LocalDate reportDate() {
    return end().toLocalDate();
  }

}
