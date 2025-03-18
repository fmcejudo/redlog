package io.github.fmcejudo.redlogs.card;

import java.util.List;

public abstract class AbstractCardQueryRequest implements CardQueryRequest{

  private final CardQuery cardQuery;
  private final CardMetadata cardMetadata;

  public AbstractCardQueryRequest(CardQuery cardQuery, CardMetadata cardMetadata) {
    this.cardQuery = cardQuery;
    this.cardMetadata = cardMetadata;
  }

  @Override
  public String id() {
    return cardQuery.id();
  }

  @Override
  public String description() {
    return cardQuery.description();
  }

  @Override
  public String executionId() {
    return cardMetadata.executionId();
  }

  @Override
  public String processor() {
    return cardQuery.processor();
  }

  @Override
  public List<String> tags() {
    return cardQuery.tags();
  }

  @Override
  public CardMetadata metadata() {
    return cardMetadata;
  }

}
