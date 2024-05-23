package com.github.fmcejudo.redlogs.client.loki.query;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
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


    private final Supplier<HttpQueryInstantClient> queryInstantClientSupplier;

    public QueryInstantClient(final WebClient.Builder webClientBuilder) {

        queryInstantClientSupplier = () -> {
            WebClientAdapter webClientAdapter = WebClientAdapter.create(webClientBuilder.build());
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
            return factory.createClient(HttpQueryInstantClient.class);
        };
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {

        long epochMilli = LocalDateTime.of(lokiRequest.reportDate(), LocalTime.of(7, 0, 0)).toInstant(UTC).toEpochMilli();
        long time = TimeUnit.MILLISECONDS.toNanos(epochMilli);
        return queryInstantClientSupplier.get().queryService(lokiRequest.query(), time);
    }

}

interface HttpQueryInstantClient {

    @GetExchange(url = "/loki/api/v1/query")
    public abstract QueryInstantResponse queryService(@RequestParam(name = "query") String query,
                                                      @RequestParam(name = "time") long time);
}
