package com.github.fmcejudo.redlogs.card.engine.process;

import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.card.engine.model.CardType;
import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import com.github.fmcejudo.redlogs.client.loki.instant.QueryInstantClient;
import com.github.fmcejudo.redlogs.client.loki.range.QueryRangeClient;
import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.apache.logging.log4j.util.Base64Util.encode;

class LokiCardProcessor implements CardProcessor {

    private final LokiClientFactory lokiClientFactory;

    private final LokiLinkBuilder lokiLinkBuilder;

    public LokiCardProcessor(RedLogLokiConfig redLogLokiConfig) {
        this.lokiClientFactory = LokiClientFactory.createInstance(redLogLokiConfig);
        this.lokiLinkBuilder =
                LokiLinkBuilder.builder(redLogLokiConfig.getDashboardUrl(), redLogLokiConfig.getDatasourceName());
    }

    public CardQueryResponse process(final CardQueryRequest cardQuery) {

        LokiClient lokiClient = lokiClientFactory.get(cardQuery.cardType());
        try {
            LokiResponse lokiResponse = lokiClient.query(new LokiRequest(cardQuery.query(), cardQuery.reportDate()));
            return composeResult(cardQuery, lokiResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error querying to loki: " + cardQuery.id(), e);
        }
    }

    private CardQueryResponse composeResult(final CardQueryRequest cardQuery, final LokiResponse lokiResponse) {

        String id = cardQuery.id();
        String description = cardQuery.description();
        String applicationName = cardQuery.applicationName();
        LocalDate reportDate = cardQuery.reportDate();

        if (lokiResponse == null) {
            System.err.println("loki response is null");
            return CardQueryResponse.failure(applicationName, reportDate, id, description, "No report response found");
        }

        if (lokiResponse.isSuccess()) {
            return buildCardReportEntries(cardQuery, lokiResponse, reportDate);
        }
        System.err.println("loki response has failed");
        return CardQueryResponse.failure(applicationName, reportDate, id, description, "query ended up being failed");
    }

    private CardQueryResponse buildCardReportEntries(final CardQueryRequest cardQuery, final LokiResponse lokiResponse,
                                                     final LocalDate reportDate) {
        String id = cardQuery.id();
        String description = cardQuery.description();
        String applicationName = cardQuery.applicationName();
        String link = lokiLinkBuilder.query(cardQuery.query())
                .from(cardQuery.reportDate().minusDays(1))
                .to(cardQuery.reportDate())
                .build();

        List<CardQueryResponseEntry> entries = lokiResponse.result().stream()
                .map(result -> new CardQueryResponseEntry(result.labels(), result.count()))
                .toList();

        return CardQueryResponse.success(applicationName, reportDate, id, description, link, entries);
    }


}

@FunctionalInterface
interface LokiClientFactory {

    public abstract LokiClient get(final CardType cardType);

    static LokiClientFactory createInstance(final RedLogLokiConfig redLogLokiConfig) {
        WebClient.Builder webClientBuilder = createWebClient(redLogLokiConfig);
        return (cardType) -> switch (cardType) {
            case SUMMARY -> new QueryRangeClient(webClientBuilder);
            case COUNT -> new QueryInstantClient(webClientBuilder);
        };
    }

    private static WebClient.Builder createWebClient(final RedLogLokiConfig redLogLokiConfig) {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        HttpClient client = HttpClient.create().responseTimeout(Duration.ofSeconds(10));
        return WebClient.builder()
                .defaultHeader("X-Grafana-Org-Id", "1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        buildBasicAuthorizationValue(redLogLokiConfig)
                )
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(redLogLokiConfig.getUrl());
    }

    private static String buildBasicAuthorizationValue(RedLogLokiConfig redLogLokiConfig) {
        return "Basic " + encode(String.join(":", redLogLokiConfig.getUsername(), redLogLokiConfig.getPassword()));
    }
}
