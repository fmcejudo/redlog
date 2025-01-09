package io.github.fmcejudo.redlogs.loki.card;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import org.apache.commons.lang3.StringUtils;

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
    String query = cardQuery.properties().get("query");
    if (StringUtils.isBlank(query)) {
      throw new IllegalStateException("query can not be null or empty");
    }
    return query;
  }

  public List<String> groupBy() {
    String groupByLabels = cardQuery.properties().get("groupByLabels");
    if (StringUtils.isBlank(groupByLabels)) {
      return List.of();
    }
    return Stream.of(groupByLabels.split(",")).map(String::trim).toList();
  }

  public String range() {
    String queryRange = cardQuery.properties().get("queryRange");
    if (StringUtils.isBlank(queryRange)) {
      throw new IllegalStateException("count card request requires queryRange property");
    }
    return queryRange;
  }

  public int expectedAtLeast() {
    String expectedAtLeast = cardQuery.properties().getOrDefault("expectedAtLeast", "1");
    return Integer.parseInt(expectedAtLeast);
  }

  public String executableQuery() {

    if (groupBy().isEmpty()) {
      return """
          count_over_time(
            %s
            | json [%s]
          )""".formatted(String.join(",", groupBy()), query());
    }

    return """
          sum by(%s) (count_over_time(
            %s
            | json [%s]
          ))
          """.formatted(String.join(",", groupBy()), query(), range());
  }
}
