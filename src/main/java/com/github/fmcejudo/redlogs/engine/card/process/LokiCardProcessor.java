package com.github.fmcejudo.redlogs.engine.card.process;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponseEntry;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.ToLongFunction;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneOffset.UTC;

class LokiCardProcessor implements CardProcessor {

    private final LokiClient lokiClient;

    LokiCardProcessor(final LokiClient lokiClient) {
        this.lokiClient = lokiClient;
    }

    public CardQueryResponse process(final CardQueryRequest cardQuery, final LocalDate reportDate) {
        String query = cardQuery.query();

        var type = switch (cardQuery.cardType()) {
            case SERVICE -> LokiRequest.RequestType.POINT_IN_TIME;
            case COUNT -> LokiRequest.RequestType.INSTANT;
        };

        try {
            LokiResponse lokiResponse = lokiClient.query(new LokiRequest(type, query, reportDate));
            return composeResult(cardQuery, lokiResponse, reportDate);
        } catch (Exception e) {
            throw new RuntimeException("Error querying to loki: " + cardQuery.id(), e);
        }
    }

    private CardQueryResponse composeResult(final CardQueryRequest cardQuery, final LokiResponse lokiResponse,
                                            final LocalDate reportDate) {

        String id = cardQuery.id();
        String description = cardQuery.description();
        String applicationName = cardQuery.applicationName();
        ;
        if (lokiResponse == null) {
            System.err.println("loki response is null");
            return CardQueryResponse.failure(applicationName, reportDate, id, description, "No report response found");
        }

        if (lokiResponse.isSuccess()) {
            return buildCardReportEntries(cardQuery, lokiResponse, reportDate);
        }
        System.err.println("loki response has failed");
        return CardQueryResponse.failure(applicationName,reportDate, id, description, "query ended up being failed");
    }

    private CardQueryResponse buildCardReportEntries(final CardQueryRequest cardQuery, final LokiResponse lokiResponse,
                                                     final LocalDate reportDate) {
        String id = cardQuery.id();
        String description = cardQuery.description();
        String applicationName = cardQuery.applicationName();
        String link = createLokiLink(cardQuery);

        List<CardQueryResponseEntry> entries = lokiResponse.result().stream()
                .map(result -> new CardQueryResponseEntry(result.labels(), result.count()))
                .toList();

        return CardQueryResponse.success(applicationName, reportDate, id, description, link, entries);
    }

    private String createLokiLink(CardQueryRequest cardQuery) {
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0, 0));
        String datasource = lokiClient.getLokiDataSource();
        return LokiLinkBuilder.builder(lokiClient.getLokiUrl())
                .query(cardQuery.query())
                .from(today.minusDays(1))
                .to(today).datasource(datasource).build();
    }
}


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
