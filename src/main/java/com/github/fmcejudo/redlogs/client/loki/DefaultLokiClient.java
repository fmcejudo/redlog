package com.github.fmcejudo.redlogs.client.loki;

import com.github.fmcejudo.redlogs.client.loki.query.QueryInstantClient;
import com.github.fmcejudo.redlogs.client.loki.range.QueryRangeClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import static org.apache.logging.log4j.util.Base64Util.encode;

public class DefaultLokiClient implements LokiClient {

    private final WebClient.Builder webClientBuilder;

    private final LokiConfig lokiConfig;

    public DefaultLokiClient(final LokiConfig lokiConfig) {

        this.lokiConfig = lokiConfig;

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        HttpClient client = HttpClient.create().responseTimeout(Duration.ofSeconds(5));
        webClientBuilder = WebClient.builder()
                .defaultHeader("X-Grafana-Org-Id", "1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Basic " + encode(String.join(":", lokiConfig.getUsername(), lokiConfig.getPassword()))
                )
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(lokiConfig.getUrl());
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {
        return switch (lokiRequest.requestType()) {
            case INSTANT -> new QueryInstantClient(lokiConfig).query(lokiRequest);
            case RANGE, POINT_IN_TIME -> new QueryRangeClient(webClientBuilder).query(lokiRequest);
        };
    }
}
