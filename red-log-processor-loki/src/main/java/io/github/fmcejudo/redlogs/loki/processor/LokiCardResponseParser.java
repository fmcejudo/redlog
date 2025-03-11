package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDate;
import java.util.function.BiFunction;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;

@FunctionalInterface
interface LokiCardResponseParser<T extends CardQueryRequest> extends BiFunction<LokiResponse, T, CardQueryResponse> {

  @Override
  default CardQueryResponse apply(LokiResponse lokiResponse, T cardRequest) {
    return this.parse(lokiResponse, cardRequest);
  }

  public abstract CardQueryResponse parse(LokiResponse lokiResponse, T cardRequest);

  static <T extends CardQueryRequest> LokiCardResponseParser<T> createParser(Class<T> clazz) {
    if (LokiSummaryCardRequest.class.equals(clazz)) {
      return (LokiCardResponseParser<T>) new SummaryCardResponseParser();
    } else if (LokiCountCardRequest.class.equals(clazz)) {
      return (LokiCardResponseParser<T>) new CountCardResponseParser();
    }
    throw new IllegalStateException();
  }

  default LokiCardResponseParser<T> withLink(String link) {
    return (response, cardQueryRequest) -> {
      CardQueryResponse cqr = this.parse(response, cardQueryRequest);
      return new CardQueryResponse(
          cqr.date(), cqr.id(), cqr.executionId(), cqr.description(), cqr.tags(), cqr.currentEntries(), link, cqr.error()
      );
    };
  }

  default CardQueryResponse createFailureResponse(CardQueryRequest cardQueryRequest, LocalDate date) {
    return CardQueryResponse.from(cardQueryRequest).withDate(date).failure("error retrieving data from loki");
  }
}
