package io.github.fmcejudo.redlogs.processor.loki;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.ToLongFunction;

import org.springframework.web.util.UriComponentsBuilder;

final class LokiLinkBuilder {

  private final String lokiExploreUrl;

  private String query;

  private LocalDateTime from;

  private LocalDateTime to;

  private final String dataSource;

  private LokiLinkBuilder(final String lokiUrl, final String dataSource) {
    this.lokiExploreUrl = String.join("/", lokiUrl, "explore");
    this.dataSource = dataSource;
  }

  public static LokiLinkBuilder builder(final String lokiUrl, final String dataSource) {
    return new LokiLinkBuilder(lokiUrl, dataSource);
  }

  public LokiLinkBuilder from(LocalDateTime from) {
    this.from = from;
    return this;
  }

  public LokiLinkBuilder query(String query) {
    this.query = query;
    return this;
  }

  public LokiLinkBuilder to(LocalDateTime to) {
    this.to = to;
    return this;
  }

  public String build() {

    String left = new LeftPart(dataSource, List.of(new QueryPart("A", query, dataSource)), new RangePart(from, to)).toString();
    String path = UriComponentsBuilder.fromPath(null).queryParam("schemaVersion", 1)
        .queryParam("panes", "{\"tm7\":" + left + "}")
        .queryParam("orgId", 1)
        .build().encode().toUriString()
        .substring(1)
        .replace("(", "%28").replace(")", "%29").replace("?", "%3F").replace("*", "%2A").replace("%0A", "%5Cn");
    return lokiExploreUrl + "?" + path;
  }

  record LeftPart(String datasource, List<QueryPart> queries, RangePart range) {

    @Override
    public String toString() {
      return """
          {"datasource":"%s","queries":%s, "range": %s}""".formatted(datasource, queries, range);
    }
  }

  record QueryPart(String refId, String expr, String datasource) {

    @Override
    public String toString() {

      String formattedExpression = expr.replace("\\", "\\\\")
          .replace("\"", "\\\"");
      return """
          {"refId":"%s","expr":"%s","queryType":"range",\
          "datasource":{"type":"loki","uid":"%s"},"editorMode":"code"}"""
          .formatted(refId, formattedExpression, datasource);
    }
  }

  record RangePart(LocalDateTime from, LocalDateTime to) {

    @Override
    public String toString() {

      ToLongFunction<LocalDateTime> dateTimeConverter = dateTime -> dateTime.toInstant(UTC).toEpochMilli();

      return """
          {"from":"%s", "to":"%s"}"""
          .formatted(dateTimeConverter.applyAsLong(from), dateTimeConverter.applyAsLong(to));
    }
  }
}
