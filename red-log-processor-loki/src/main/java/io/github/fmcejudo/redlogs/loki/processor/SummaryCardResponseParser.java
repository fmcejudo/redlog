package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse.LokiResult;

class SummaryCardResponseParser implements LokiCardResponseParser<LokiSummaryCardRequest> {

  @Override
  public CardQueryResponse parse(LokiResponse lokiResponse, LokiSummaryCardRequest cardRequest) {
    LocalDateTime date = cardRequest.metadata().endTime();
    if (lokiResponse.isSuccess()) {
      return createSuccessResponse(lokiResponse, cardRequest, date.toLocalDate());
    } else {
      return createFailureResponse(cardRequest, date.toLocalDate());
    }
  }

  private CardQueryResponse createSuccessResponse(LokiResponse response, LokiSummaryCardRequest cardQueryRequest, LocalDate date) {
    List<CardQueryResponseEntry> entries = response.result().stream()
        .map(r -> createResponseEntryWithDefinedLabels(cardQueryRequest.showLabels(), r))
        .toList();

    return CardQueryResponse.success(
        date, cardQueryRequest.id(), cardQueryRequest.executionId(), cardQueryRequest.description(), "", cardQueryRequest.tags(), entries
    );
  }

  private CardQueryResponseEntry createResponseEntryWithDefinedLabels(List<String> definedLabels, LokiResult r) {
    if (definedLabels.isEmpty()) {
      return new CardQueryResponseEntry(r.labels(), r.count());
    }

    Map<String, String> subsetLabels = r.labels().entrySet().stream().filter(e -> definedLabels.contains(e.getKey()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return new CardQueryResponseEntry(subsetLabels, r.count());
  }

}
