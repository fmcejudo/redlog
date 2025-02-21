package io.github.fmcejudo.redlogs.loki.card;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.AbstractCardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import org.apache.commons.lang3.StringUtils;

public class LokiSummaryCardRequest extends AbstractCardQueryRequest implements CardQueryRequest {

  private final CardQuery cardQuery;

  private LokiSummaryCardRequest(CardQuery cardQuery, CardMetadata cardMetadata) {
    super(cardQuery, cardMetadata);
    this.cardQuery = cardQuery;
  }

  public static LokiSummaryCardRequest from(CardQuery cardQuery, CardMetadata cardMetadata) {
    String type = Objects.requireNonNull(cardQuery.properties().get("type"));
    if (!type.equalsIgnoreCase("summary")) {
      throw new RuntimeException("Illegal card creation");
    }
    return new LokiSummaryCardRequest(cardQuery, cardMetadata);
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return null;
  }

  public String query() {
    String query = cardQuery.properties().get("query");
    if (StringUtils.isBlank(query)) {
      throw new IllegalStateException("query can not be null or empty");
    }
    if (query.contains("| json")) {
      return query;
    }
    return query.concat(" | json");
  }

  public List<String> showLabels() {
    String showLabels = cardQuery.properties().get("showLabels");
    if (StringUtils.isBlank(showLabels)) {
      return List.of();
    }
    return Stream.of(showLabels.split(",")).map(String::trim).toList();
  }

}
