package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDate;
import java.util.List;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;

class CountCardResponseParser implements LokiCardResponseParser<LokiCountCardRequest> {

   public CardQueryResponse parse(LokiResponse response, LokiCountCardRequest cardRequest) {
      CardMetadata metadata = cardRequest.metadata();
      LocalDate date = metadata.endTime().toLocalDate();
      if (!response.isSuccess()) {
        return createFailureResponse(cardRequest, date);
      }
      return createSuccessResponse(response, cardRequest, date);
  }

  private CardQueryResponse createSuccessResponse(LokiResponse response, LokiCountCardRequest cardQueryRequest, LocalDate date) {

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
