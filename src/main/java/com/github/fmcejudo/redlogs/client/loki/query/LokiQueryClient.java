package com.github.fmcejudo.redlogs.client.loki.query;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.time.ZoneOffset.UTC;

public final class LokiQueryClient implements LokiClient {

    private final Supplier<HttpQueryClient> lokiQueryClientSupplier;

    public LokiQueryClient(final WebClient.Builder webClientBuilder) {
        lokiQueryClientSupplier = () -> {
            WebClientAdapter webClientAdapter = WebClientAdapter.create(webClientBuilder.build());
            return HttpServiceProxyFactory.builderFor(webClientAdapter).build().createClient(HttpQueryClient.class);
        };
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {

        System.out.println(lokiRequest.query());

        long epochMilli = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0, 0)).toInstant(UTC).toEpochMilli();

        long time = TimeUnit.MILLISECONDS.toNanos(epochMilli);
        if (lokiRequest.requestType().equals(LokiRequest.RequestType.INSTANT)) {
            return lokiQueryClientSupplier.get().query(lokiRequest.query(), 1000, time);
        } else {
            return lokiQueryClientSupplier.get().queryService(lokiRequest.query(), 1000, time);
        }
    }
}

interface HttpQueryClient {

    @GetExchange(url = "/loki/api/v1/query")
    public abstract LokiQueryResponse query(@RequestParam(name = "query") String query,
                                            @RequestParam(name = "limit") int limit,
                                            @RequestParam(name = "time") long time);

    @GetExchange(url = "/loki/api/v1/query")
    public abstract LokiQueryServiceResponse queryService(@RequestParam(name = "query") String query,
                                                          @RequestParam(name = "limit") int limit,
                                                          @RequestParam(name = "time") long time);
}