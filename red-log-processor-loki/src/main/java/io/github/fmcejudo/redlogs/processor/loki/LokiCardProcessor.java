package io.github.fmcejudo.redlogs.processor.loki;

import static org.apache.logging.log4j.util.Base64Util.encode;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.domain.CounterCardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.SummaryCardQueryRequest;
import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.processor.ProcessorContext;
import io.github.fmcejudo.redlogs.card.processor.filter.ResponseEntryFilter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import io.github.fmcejudo.redlogs.processor.loki.instant.QueryInstantClient;
import io.github.fmcejudo.redlogs.processor.loki.range.QueryRangeClient;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

class LokiCardProcessor implements CardProcessor {

  private static final Logger log = LoggerFactory.getLogger(LokiCardProcessor.class);

  private final LokiClientFactory lokiClientFactory;

  private final LokiLinkBuilder lokiLinkBuilder;

  public LokiCardProcessor(Map<String, String> details) {

    LokiConnectionDetails connectionDetails = LokiConnectionDetails.from(details);
    this.lokiClientFactory = LokiClientFactory.createInstance(connectionDetails);
    this.lokiLinkBuilder =
        LokiLinkBuilder.builder(connectionDetails.dashboardUrl(), connectionDetails.datasource());
  }

  public void process(final ProcessorContext processorContext, ResponseEntryFilter filters, final CardReportWriter writer) {
      processCardQueryRequest(processorContext, filters, writer::onNext, writer::onError);
  }

  private void processCardQueryRequest(ProcessorContext processorContext,
      ResponseEntryFilter responseEntryFilter,
      Consumer<CardQueryResponse> onNext,
      Consumer<Throwable> onError) {
    LokiClient lokiClient = lokiClientFactory.get(processorContext.cardQueryRequest());
    try {
      LokiRequest lokiRequest = new LokiRequest(
          processorContext.cardQueryRequest(), processorContext.start(), processorContext.end()
      );

      LokiResponse lokiResponse = lokiClient.query(lokiRequest);
      CardQueryResponse cardQueryResponse = composeResult(processorContext, responseEntryFilter, lokiResponse);
      onNext.accept(cardQueryResponse);
    } catch (Exception e) {
      log.error("Error querying loki {}", e.getMessage());
      onError.accept(new RuntimeException("Error querying to loki: " + processorContext.id(), e));
    }
  }

  private CardQueryResponse composeResult(final ProcessorContext processorContext,
      final ResponseEntryFilter responseEntryFilter,
      final LokiResponse lokiResponse) {

    String id = processorContext.id();
    String description = processorContext.description();
    LocalDate reportDate = processorContext.reportDate();
    String executionId = processorContext.executionId();

    if (lokiResponse == null) {
      log.error("loki response is null");
      return CardQueryResponse.failure(reportDate, id, executionId, description, "No report response found");
    }

    if (lokiResponse.isSuccess()) {
      return buildCardReportEntries(processorContext, responseEntryFilter, lokiResponse, reportDate);
    }
    log.error("loki response has failed");
    return CardQueryResponse.failure(reportDate, id, executionId, description, "query ended up being failed");
  }

  private CardQueryResponse buildCardReportEntries(final ProcessorContext processorContext,
      final ResponseEntryFilter responseEntryFilter,
      final LokiResponse lokiResponse,
      final LocalDate reportDate) {
    String id = processorContext.id();
    String description = processorContext.description();
    String executionId = processorContext.executionId();
    String link = lokiLinkBuilder.query(processorContext.query())
        .from(processorContext.start())
        .to(processorContext.end())
        .build();

    List<CardQueryResponseEntry> entries = lokiResponse.result().stream()
        .map(result -> new CardQueryResponseEntry(result.labels(), result.count()))
        .filter(responseEntryFilter::filter)
        .toList();

    return CardQueryResponse.success(reportDate, id, executionId, description, link, entries);
  }

}

@FunctionalInterface
interface LokiClientFactory {

  public abstract LokiClient get(final CardQueryRequest cardQueryRequest);

  static LokiClientFactory createInstance(final LokiConnectionDetails connectionDetails) {
    WebClient.Builder webClientBuilder = createWebClient(connectionDetails);
    return (cardQueryRequest) -> {
      if (cardQueryRequest instanceof CounterCardQueryRequest) {
        return new QueryInstantClient(webClientBuilder);
      } else if (cardQueryRequest instanceof SummaryCardQueryRequest) {
        return new QueryRangeClient(webClientBuilder);
      }
      throw new RuntimeException("Unknown card query request");
    };
  }

  private static WebClient.Builder createWebClient(final LokiConnectionDetails connectionDetails) {
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
        .build();

    HttpClient client = HttpClient.create().responseTimeout(Duration.ofSeconds(10));
    return WebClient.builder()
        .defaultHeader("X-Grafana-Org-Id", "1")
        .defaultHeader(
            HttpHeaders.AUTHORIZATION,
            buildBasicAuthorizationValue(connectionDetails)
        )
        .exchangeStrategies(strategies)
        .clientConnector(new ReactorClientHttpConnector(client))
        .baseUrl(connectionDetails.url());
  }

  private static String buildBasicAuthorizationValue(final LokiConnectionDetails connectionDetails) {
    if (Strings.isBlank(connectionDetails.user()) || Strings.isBlank(connectionDetails.password())) {
      return "";
    }
    return "Basic " + encode(String.join(":", connectionDetails.user(), connectionDetails.password()));
  }
}
