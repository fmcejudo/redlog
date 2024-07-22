package com.github.fmcejudo.redlogs.client.loki.instant;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.UTC;

public final class QueryInstantClient implements LokiClient {


    private final HttpQueryInstantClient queryInstantClient;

    public QueryInstantClient(final WebClient.Builder webClientBuilder) {
        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClientBuilder.build());
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();

        this.queryInstantClient = factory.createClient(HttpQueryInstantClient.class);
    }

    @Override
    public LokiResponse query(final LokiRequest lokiRequest) {
        long epochMilli = lokiRequest.endTime().toInstant(UTC).toEpochMilli();
        long time = TimeUnit.MILLISECONDS.toNanos(epochMilli);
        return queryInstantClient.queryService(lokiRequest.query(), time);
    }


    public interface HttpQueryInstantClient {

        @GetExchange(url = "/loki/api/v1/query")
        public abstract QueryInstantResponse queryService(@RequestParam(name = "query") String query,
                                                          @RequestParam(name = "time") long time);
    }

}
