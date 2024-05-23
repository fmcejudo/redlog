package com.github.fmcejudo.redlogs.engine.card.process;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.ToLongFunction;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneOffset.UTC;

final class LokiLinkBuilder {

    private final String lokiExploreUrl;
    private String query;
    private LocalDateTime from;
    private LocalDateTime to;
    private String dataSource;

    private LokiLinkBuilder(final String lokiUrl) {
        this.lokiExploreUrl = String.join("/", lokiUrl, "explore");
    }

    public static LokiLinkBuilder builder(final String lokiUrl) {
        return new LokiLinkBuilder(lokiUrl);
    }

    public LokiLinkBuilder datasource(String dataSource) {
        this.dataSource = dataSource;
        return this;
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

        String left = new LeftPart(dataSource, List.of(new QueryPart("A", query)), new RangePart(from, to)).toString();
        return UriComponentsBuilder.fromHttpUrl(lokiExploreUrl)
                .queryParam("orgId", 1)
                .queryParam("left", left)
                .build().encode(UTF_8).toUriString();
    }


    record LeftPart(String datasource, List<QueryPart> queries, RangePart range) {
        @Override
        public String toString() {
            return """
                    {"datasource":"%s","queries":%s, "range": %s}""".formatted(datasource, queries, range);
        }
    }

    record QueryPart(String refId, String expr) {
        @Override
        public String toString() {

            String formattedQuery = expr.replace("\n", " ").trim().replaceAll(" +", " ").replace("\"", "\\\"");

            return """
                    {"refId":"%s","expr":"%s"}""".formatted(refId, formattedQuery);
        }
    }

    record RangePart(LocalDateTime from, LocalDateTime to) {

        @Override
        public String toString() {

            ToLongFunction<LocalDateTime> dateTimeConverter =
                    dateTime -> dateTime.toInstant(UTC).toEpochMilli();

            return """
                    {"from":"%s", "to":"%s"}"""
                    .formatted(dateTimeConverter.applyAsLong(from), dateTimeConverter.applyAsLong(to));
        }
    }
}
