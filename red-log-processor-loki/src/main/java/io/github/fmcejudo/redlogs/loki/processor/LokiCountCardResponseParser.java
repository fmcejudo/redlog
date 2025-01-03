package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;

@FunctionalInterface
interface LokiCountCardResponseParser extends BiFunction<LokiResponse, LokiCountCardRequest, CardQueryResponse> {

  @Override
  default CardQueryResponse apply(LokiResponse response, LokiCountCardRequest cardQueryRequest) {
    return this.parse(response, cardQueryRequest);
  }

  CardQueryResponse parse(final LokiResponse response, final LokiCountCardRequest cardQueryRequest);

  static LokiCountCardResponseParser createParser() {
    return (response, cardQueryRequest) -> {
      CardMetadata metadata = cardQueryRequest.metadata();
      LocalDate date = metadata.endTime().toLocalDate();
      if (!response.isSuccess()) {
        return createFailureResponse(cardQueryRequest, date);
      }
      return createSuccessResponse(response, cardQueryRequest, date);
    };
  }

  default LokiCountCardResponseParser withLink(String link) {
    return (response, cardQueryRequest) -> {
      CardQueryResponse cqr = this.parse(response, cardQueryRequest);
      return new CardQueryResponse(cqr.date(), cqr.id(), cqr.executionId(), cqr.description(), cqr.currentEntries(), link, cqr.error());
    };
  }

  private static CardQueryResponse createFailureResponse(CardQueryRequest cardQueryRequest, LocalDate date) {
    return CardQueryResponse.failure(
        date, cardQueryRequest.id(), cardQueryRequest.executionId(), cardQueryRequest.description(), "error"
    );
  }

  private static CardQueryResponse createSuccessResponse(LokiResponse response, LokiCountCardRequest cardQueryRequest, LocalDate date) {
    
    List<CardQueryResponseEntry> entries =
        response.result().stream()
            .map(r -> new CardQueryResponseEntry(r.labels(), r.count()))
            .filter(cre -> cre.count() >= cardQueryRequest.expectedAtLeast())
            .toList();

    return CardQueryResponse.success(
        date, cardQueryRequest.id(), cardQueryRequest.executionId(), cardQueryRequest.description(), "", entries
    );
  }
}
