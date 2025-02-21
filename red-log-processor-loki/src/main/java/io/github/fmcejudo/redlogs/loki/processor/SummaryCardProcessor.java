package io.github.fmcejudo.redlogs.loki.processor;

import java.util.Optional;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.loki.card.LokiSummaryCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClient;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory.QueryTypeEnum;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiConnectionDetails;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

class SummaryCardProcessor implements CardQueryProcessor {

  private final LokiClientFactory lokiClientFactory;

  private final String grafanaDashboard;

  private final String grafanaDatasource;

  private final LokiCardResponseParser<LokiSummaryCardRequest> lokiCardResponseParser ;

  SummaryCardProcessor(LokiClientFactory lokiClientFactory, LokiConnectionDetails lokiConnectionDetails) {
    lokiCardResponseParser = LokiCardResponseParser.createParser(LokiSummaryCardRequest.class);
    this.lokiClientFactory = lokiClientFactory;
    this.grafanaDashboard = lokiConnectionDetails.dashboardUrl();
    this.grafanaDatasource = lokiConnectionDetails.datasource();
  }

  @Override
  public CardQueryResponse process(CardQueryRequest cardQueryRequest) {
    Assert.isInstanceOf(LokiSummaryCardRequest.class, cardQueryRequest, "This processor is for summary card requests");
    LokiSummaryCardRequest cardRequest = (LokiSummaryCardRequest) cardQueryRequest;
    LokiResponse lokiResponse = executeQuery(cardRequest);
    String link = buildLink(cardRequest);
    return lokiCardResponseParser.withLink(link).parse(lokiResponse, cardRequest);
  }

  private LokiResponse executeQuery(LokiSummaryCardRequest cardRequest) {
    LokiClient lokiClient = lokiClientFactory.get(QueryTypeEnum.RANGE);
    LokiRequest lokiRequest = new LokiRequest(cardRequest.query(), cardRequest.metadata().startTime(), cardRequest.metadata().endTime());
    return lokiClient.query(lokiRequest);
  }

  private String buildLink(LokiSummaryCardRequest summaryCardRequest) {
    if (StringUtils.isBlank(grafanaDashboard)) {
      return null;
    }
    String datasource = Optional.ofNullable(grafanaDatasource).orElse("default");
    return LokiLinkBuilder.builder(grafanaDashboard, datasource)
        .query(summaryCardRequest.query())
        .from(summaryCardRequest.metadata().startTime())
        .to(summaryCardRequest.metadata().endTime())
        .build();
  }

}
