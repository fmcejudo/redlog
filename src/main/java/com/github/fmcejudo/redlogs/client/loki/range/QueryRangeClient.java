package com.github.fmcejudo.redlogs.client.loki.range;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class QueryRangeClient implements LokiClient {

    private final Supplier<HttpQueryRangeClient> queryRangeClientSupplier;

    public QueryRangeClient(final WebClient.Builder webClientBuilder) {
        queryRangeClientSupplier = () -> {
            WebClientAdapter webClientAdapter = WebClientAdapter.create(webClientBuilder.build());
            return HttpServiceProxyFactory.builderFor(webClientAdapter).build()
                    .createClient(HttpQueryRangeClient.class);
        };
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {

        LocalDateTime now = LocalDateTime.of(lokiRequest.reportDate(), LocalTime.of(7, 0, 0));
        long start = TimeUnit.MILLISECONDS.toNanos(now.minusHours(24).toInstant(ZoneOffset.UTC).toEpochMilli());
        long end = TimeUnit.MILLISECONDS.toNanos(now.toInstant(ZoneOffset.UTC).toEpochMilli());
        return queryRangeClientSupplier.get().queryService(lokiRequest.query(), 1000, start, end, "1m");
    }
}

interface HttpQueryRangeClient {

    @GetExchange(url = "/loki/api/v1/query_range")
    public abstract QueryRangeResponse query(@RequestParam(name = "query") String query,
                                       @RequestParam(name = "limit") int limit,
                                       @RequestParam(name = "start") long start,
                                       @RequestParam(name = "end") long end,
                                       @RequestParam(name = "step") String step);

    @GetExchange(url = "/loki/api/v1/query_range")
    public abstract QueryRangeServiceResponse queryService(@RequestParam(name = "query") String query,
                                              @RequestParam(name = "limit") int limit,
                                              @RequestParam(name = "start") long start,
                                              @RequestParam(name = "end") long end,
                                              @RequestParam(name = "step") String step);
}
