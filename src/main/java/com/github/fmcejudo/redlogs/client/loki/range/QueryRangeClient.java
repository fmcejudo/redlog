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

public class QueryRangeClient implements LokiClient {

    private final HttpQueryRangeClient queryRangeClient;

    public QueryRangeClient(final WebClient.Builder webClientBuilder) {
        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClientBuilder.build());
        this.queryRangeClient = HttpServiceProxyFactory.builderFor(webClientAdapter).build()
                .createClient(HttpQueryRangeClient.class);
    }

    @Override
    public LokiResponse query(LokiRequest lokiRequest) {

        LocalDateTime now = LocalDateTime.of(lokiRequest.reportDate(), LocalTime.of(23, 0, 0));
        long start = TimeUnit.MILLISECONDS.toNanos(now.minusHours(24).toInstant(ZoneOffset.UTC).toEpochMilli());
        long end = TimeUnit.MILLISECONDS.toNanos(now.toInstant(ZoneOffset.UTC).toEpochMilli());
        return queryRangeClient.queryService(lokiRequest.query(), 1000, start, end, "1m");
    }

    public interface HttpQueryRangeClient {

        @GetExchange(url = "/loki/api/v1/query_range")
        public abstract QueryRangeResponse queryService(@RequestParam(name = "query") String query,
                                                        @RequestParam(name = "limit") int limit,
                                                        @RequestParam(name = "start") long start,
                                                        @RequestParam(name = "end") long end,
                                                        @RequestParam(name = "step") String step);
    }
}


