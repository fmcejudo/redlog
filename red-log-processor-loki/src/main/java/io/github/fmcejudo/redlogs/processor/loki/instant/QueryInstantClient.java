package io.github.fmcejudo.redlogs.processor.loki.instant;

import static java.time.ZoneOffset.UTC;

import java.util.concurrent.TimeUnit;

import io.github.fmcejudo.redlogs.processor.loki.LokiClient;
import io.github.fmcejudo.redlogs.processor.loki.LokiRequest;
import io.github.fmcejudo.redlogs.processor.loki.LokiResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public final class QueryInstantClient implements LokiClient {


    private final HttpQueryInstantClient queryInstantClient;

    public QueryInstantClient(final RestClient restClient) {
        HttpExchangeAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        this.queryInstantClient = factory.createClient(HttpQueryInstantClient.class);
    }

    @Override
    public LokiResponse query(final LokiRequest lokiRequest) {
        String query = lokiRequest.getQuery();
        long epochMilli = lokiRequest.endTime().toInstant(UTC).toEpochMilli();
        long time = TimeUnit.MILLISECONDS.toNanos(epochMilli);
        return queryInstantClient.queryService(query, time);
    }


    public interface HttpQueryInstantClient {

        @GetExchange(url = "/loki/api/v1/query")
        public abstract QueryInstantResponse queryService(@RequestParam(name = "query") String query,
                                                          @RequestParam(name = "time") long time);
    }

}
