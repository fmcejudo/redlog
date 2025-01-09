package com.github.fmcejudo.redlogs.card.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.RangeParseException;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import org.springframework.util.NumberUtils;

class CardMetadataParser {

  CardMetadata parse(CardContext cardContext, CardFile cardFile) {
    String executionId = UUID.randomUUID().toString();
    LocalDateTime startTime = startTime(cardContext.reportDate(), cardFile.time(), cardFile.range());
    LocalDateTime endTime = endTime(cardContext.reportDate(), cardFile.time());
    return new CardMetadata(executionId, cardContext.applicationName(), startTime, endTime);
  }

  private LocalDateTime startTime(LocalDate date, LocalTime time, String range) {

    char temporalChar = range.charAt(range.length() - 1);
    String amountString = range.substring(0, range.length() - 1);

    TemporalUnit temporalUnit = switch (temporalChar) {
      case 'h' -> ChronoUnit.HOURS;
      case 'm' -> ChronoUnit.MINUTES;
      default -> throw new RangeParseException("range temporal unit is not valid. Only 'h' for hours and 'm' for minutes is allowed");
    };

    try {
      Integer amount = NumberUtils.parseNumber(amountString, Integer.class);
      return LocalDateTime.of(date, time).minus(amount, temporalUnit);
    } catch (NumberFormatException e) {
      throw new RangeParseException("amount %s is not a valid number");
    }
  }

  private LocalDateTime endTime(LocalDate date, LocalTime time) {
    return LocalDateTime.of(date, time);
  }
}
