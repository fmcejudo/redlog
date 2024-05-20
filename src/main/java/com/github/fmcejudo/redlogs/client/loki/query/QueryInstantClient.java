package com.github.fmcejudo.redlogs.client.loki.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.UTC;
import static org.apache.logging.log4j.util.Base64Util.encode;

public final class QueryInstantClient implements LokiClient {


    private final RedLogLokiConfig config;
    private final ObjectMapper objectMapper;

    public QueryInstantClient(final RedLogLokiConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {

        long epochMilli = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0, 0)).toInstant(UTC).toEpochMilli();

        try (HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {

            long time = TimeUnit.MILLISECONDS.toNanos(epochMilli);
            URI uri = UriComponentsBuilder.fromHttpUrl(config.getUrl())
                    .path("/loki/api/v1/query")
                    .queryParam("query", lokiRequest.query(), StandardCharsets.UTF_8)
                    .queryParam("time", time).build().toUri();

            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header("X-Grafana-Org-Id", "1")
                    .header(HttpHeaders.AUTHORIZATION,
                            "Basic " + encode(String.join(":", config.getUsername(), config.getPassword()))
                    ).GET().build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                throw new RuntimeException(response.body());
            }
            return objectMapper.readValue(response.body(), LokiQueryResponse.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}