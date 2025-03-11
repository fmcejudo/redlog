package io.github.fmcejudo.redlogs.card;

import java.time.LocalDate;
import java.util.List;

public record CardQueryResponse(
    LocalDate date,
    String id,
    String executionId,
    String description,
    List<String> tags,
    List<CardQueryResponseEntry> currentEntries,
    String link,
    String error) {

  public static CardQueryResponseBuilder from(CardQueryRequest cardQueryRequest) {
    return new CardQueryResponseBuilder(cardQueryRequest);
  }

  public static class CardQueryResponseBuilder {

    private LocalDate date;

    private final String id;

    private final String executionId;

    private final String description;

    private final List<String> tags;

    private CardQueryResponseBuilder(CardQueryRequest cardQueryRequest) {
      this.id = cardQueryRequest.id();
      this.executionId = cardQueryRequest.executionId();
      this.description = cardQueryRequest.description();
      this.tags = cardQueryRequest.tags();
      this.date = LocalDate.now();
    }

    public CardQueryResponseBuilder withDate(final LocalDate date) {
      this.date = date;
      return this;
    }

    public CardQueryResponse failure(String error) {
      return new CardQueryResponse(date, id, executionId, description, tags, List.of(), null, error);
    }

    public CardQueryResponse success(String link, List<CardQueryResponseEntry> entries) {
      return new CardQueryResponse(date, id, executionId, description, tags, entries, link, null);
    }
  }

}

