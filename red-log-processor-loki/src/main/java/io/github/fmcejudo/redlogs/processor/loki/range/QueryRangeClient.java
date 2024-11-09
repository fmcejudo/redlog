package io.github.fmcejudo.redlogs.processor.loki.range;

import io.github.fmcejudo.redlogs.processor.loki.LokiClient;
import io.github.fmcejudo.redlogs.processor.loki.LokiRequest;
import io.github.fmcejudo.redlogs.processor.loki.LokiResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

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
        String query = lokiRequest.getQuery();
        long start = TimeUnit.MILLISECONDS.toNanos(lokiRequest.startTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        long end = TimeUnit.MILLISECONDS.toNanos(lokiRequest.endTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        return queryRangeClient.queryService(query, 1000, start, end, "1m");
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


