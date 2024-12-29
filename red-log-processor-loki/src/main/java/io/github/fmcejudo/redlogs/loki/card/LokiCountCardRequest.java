package io.github.fmcejudo.redlogs.loki.card;

import java.util.Objects;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public class LokiCountCardRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  private final CardMetadata cardMetadata;

  private LokiCountCardRequest(CardQuery cardQuery, CardMetadata cardMetadata) {
    this.cardQuery = cardQuery;
    this.cardMetadata = cardMetadata;
  }

  public static LokiCountCardRequest from(CardQuery cardQuery, CardMetadata cardMetadata) {
    String type = Objects.requireNonNull(cardQuery.properties().get("type"));
    if (!type.equalsIgnoreCase("count")) {
      throw new RuntimeException("Illegal card creation");
    }
    return new LokiCountCardRequest(cardQuery, cardMetadata);
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
  public CardMetadata metadata() {
    return cardMetadata;
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return null;
  }

  public String query() {
    return cardQuery.properties().get("query");
  }

  public String grafanaDashboard() {
    return cardQuery.properties().get("grafana-dashboard");
  }

  public String grafanaDatasource() {
    return cardQuery.properties().get("datasource");
  }
}
