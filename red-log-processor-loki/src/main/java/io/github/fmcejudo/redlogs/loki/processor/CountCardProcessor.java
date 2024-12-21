package io.github.fmcejudo.redlogs.loki.processor;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.loki.card.LokiCountCardRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiClientFactory.QueryTypeEnum;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiRequest;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;
import org.springframework.util.Assert;

class CountCardProcessor implements CardQueryProcessor {

  private final LokiClientFactory lokiClientFactory;

  private final CardQueryResponseParser cardQueryResponseParser;

  CountCardProcessor(LokiClientFactory lokiClientFactory) {
    this.lokiClientFactory = lokiClientFactory;
    this.cardQueryResponseParser = CardQueryResponseParser.createParser();
  }

  @Override
  public CardQueryResponse process(CardQueryRequest cardQueryRequest) {
    Assert.isInstanceOf(LokiCountCardRequest.class, cardQueryRequest);
    LokiRequest lokiRequest = createLokiRequest((LokiCountCardRequest) cardQueryRequest);
    LokiResponse response = lokiClientFactory.get(QueryTypeEnum.INSTANT).query(lokiRequest);
    return cardQueryResponseParser.parse(response, cardQueryRequest);
  }

  private LokiRequest createLokiRequest(LokiCountCardRequest lokiCountCardRequest) {
    return new LokiRequest(
        lokiCountCardRequest.query(), lokiCountCardRequest.metadata().startTime(), lokiCountCardRequest.metadata().endTime()
    );
  }

}
