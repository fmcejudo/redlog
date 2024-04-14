package com.github.fmcejudo.redlogs.client.loki;

import com.github.fmcejudo.redlogs.client.loki.query.LokiQueryClient;
import com.github.fmcejudo.redlogs.client.loki.range.QueryRangeClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static org.apache.logging.log4j.util.Base64Util.encode;

@Component
public class DefaultLokiClient implements LokiClient {

    private final WebClient.Builder webClientBuilder;

    public DefaultLokiClient(final LokiConfig lokiConfig) {

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        webClientBuilder = WebClient.builder()
                .defaultHeader("X-Grafana-Org-Id", "1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Basic " + encode(String.join(":", lokiConfig.getUsername(), lokiConfig.getPassword()))
                )
                .exchangeStrategies(strategies)
                .baseUrl(lokiConfig.getUrl());
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {
        return switch (lokiRequest.requestType()) {
            case INSTANT -> new LokiQueryClient(webClientBuilder).query(lokiRequest);
            case RANGE, POINT_IN_TIME -> new QueryRangeClient(webClientBuilder).query(lokiRequest);
        };
    }
}
