package io.github.fmcejudo.redlogs.card;

import java.time.LocalDate;
import java.util.List;

public class CardQueryResponse {

  private final LocalDate date;
  private final String id;
  private final String executionId;
  private final String description;
  private final List<String> tags;
  private final List<CardQueryResponseEntry> currentEntries;
  private final String link;
  private final String error;

  private CardQueryResponse(LocalDate date, String id, String executionId, String description, List<String> tags,
      List<CardQueryResponseEntry> currentEntries, String link, String error) {
    this.date = date;
    this.id = id;
    this.executionId = executionId;
    this.description = description;
    this.tags = tags;
    this.currentEntries = currentEntries;
    this.link = link;
    this.error = error;
  }

  public LocalDate date() {
    return date;
  }

  public String id() {
    return id;
  }

  public String executionId() {
    return executionId;
  }

  public String description() {
    return description;
  }

  public List<String> tags() {
    return tags;
  }

  public List<CardQueryResponseEntry> currentEntries() {
    return currentEntries;
  }

  public String link() {
    return link;
  }

  public String error() {
    return error;
  }

  public CardQueryResponse withLink(String link) {
    return new CardQueryResponse(this.date, this.id, this.executionId, this.description, this.tags, this.currentEntries, link, this.error);
  }

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

