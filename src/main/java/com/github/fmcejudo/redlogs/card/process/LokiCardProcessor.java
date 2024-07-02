package com.github.fmcejudo.redlogs.card.process;

import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.card.model.CardType;
import com.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import com.github.fmcejudo.redlogs.client.loki.instant.QueryInstantClient;
import com.github.fmcejudo.redlogs.client.loki.range.QueryRangeClient;
import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.logging.log4j.util.Base64Util.encode;

class LokiCardProcessor implements CardProcessor {

    private final LokiClientFactory lokiClientFactory;

    private final LokiLinkBuilder lokiLinkBuilder;

    public LokiCardProcessor(RedLogLokiConfig redLogLokiConfig) {
        this.lokiClientFactory = LokiClientFactory.createInstance(redLogLokiConfig);
        this.lokiLinkBuilder =
                LokiLinkBuilder.builder(redLogLokiConfig.getDashboardUrl(), redLogLokiConfig.getDatasourceName());
    }

    public void process(final CardRequest cardRequest, final CardResponseWriter writer) {

        //Write first to execution collection
        String executionId = UUID.randomUUID().toString();
        CardRequest cr = cardRequest.withExecutionId(executionId);
        writer.writeExecution(cr);

        //For each query request, process it
        cardRequest.cardQueryRequests().forEach(cqr -> {
            ProcessorContext processorContext = new ProcessorContext(cr, cqr);
            processCardQueryRequest(processorContext, writer::onNext, writer::onError);
        });
        writer.onComplete();
    }

    private void processCardQueryRequest(ProcessorContext processorContext,
                                         Consumer<CardQueryResponse> onNext,
                                         Consumer<Throwable> onError) {
        LokiClient lokiClient = lokiClientFactory.get(processorContext.type());
        try {
            LokiRequest lokiRequest =
                    new LokiRequest(processorContext.query(), processorContext.start(), processorContext.end());
            LokiResponse lokiResponse = lokiClient.query(lokiRequest);
            CardQueryResponse cardQueryResponse = composeResult(processorContext, lokiResponse);
            onNext.accept(cardQueryResponse);
        } catch (Exception e) {
            onError.accept(new RuntimeException("Error querying to loki: " + processorContext.id(), e));
        }
    }

    private CardQueryResponse composeResult(final ProcessorContext processorContext, final LokiResponse lokiResponse) {

        String id = processorContext.id();
        String description = processorContext.description();
        String applicationName = processorContext.applicationName();
        LocalDate reportDate = processorContext.reportDate();
        String executionId = processorContext.executionId();

        if (lokiResponse == null) {
            System.err.println("loki response is null");
            return CardQueryResponse.failure(
                    applicationName, reportDate, id, executionId, description, "No report response found"
            );
        }

        if (lokiResponse.isSuccess()) {
            return buildCardReportEntries(processorContext, lokiResponse, reportDate);
        }
        System.err.println("loki response has failed");
        return CardQueryResponse.failure(
                applicationName, reportDate, id, executionId, description, "query ended up being failed"
        );
    }

    private CardQueryResponse buildCardReportEntries(final ProcessorContext processorContext,
                                                     final LokiResponse lokiResponse,
                                                     final LocalDate reportDate) {
        String id = processorContext.id();
        String description = processorContext.description();
        String applicationName = processorContext.applicationName();
        String executionId = processorContext.executionId();
        String link = lokiLinkBuilder.query(processorContext.query())
                .from(processorContext.start())
                .to(processorContext.end())
                .build();

        List<CardQueryResponseEntry> entries = lokiResponse.result().stream()
                .map(result -> new CardQueryResponseEntry(result.labels(), result.count()))
                .toList();

        return CardQueryResponse.success(applicationName, reportDate, id, executionId, description, link, entries);
    }

}

@FunctionalInterface
interface LokiClientFactory {

    public abstract LokiClient get(final CardType cardType);

    static LokiClientFactory createInstance(final RedLogLokiConfig redLogLokiConfig) {
        WebClient.Builder webClientBuilder = createWebClient(redLogLokiConfig);
        return (cardType) -> switch (cardType) {
            case SUMMARY -> new QueryRangeClient(webClientBuilder);
            case COUNT -> new QueryInstantClient(webClientBuilder);
        };
    }

    private static WebClient.Builder createWebClient(final RedLogLokiConfig redLogLokiConfig) {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        HttpClient client = HttpClient.create().responseTimeout(Duration.ofSeconds(10));
        return WebClient.builder()
                .defaultHeader("X-Grafana-Org-Id", "1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        buildBasicAuthorizationValue(redLogLokiConfig)
                )
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(redLogLokiConfig.getUrl());
    }

    private static String buildBasicAuthorizationValue(RedLogLokiConfig redLogLokiConfig) {
        return "Basic " + encode(String.join(":", redLogLokiConfig.getUsername(), redLogLokiConfig.getPassword()));
    }
}
