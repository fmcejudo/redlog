package com.github.fmcejudo.redlogs.card.runner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;

@FunctionalInterface
public interface CardRunner {

  void onCardContext(CardContext cardContext);

  public static CardLoader load(Function<CardContext, CardFile> cardLoader) {
    return cardLoader::apply;
  }

  @FunctionalInterface
  interface CardLoader {

    CardFile get(CardContext cardContext);

    default CardTransformer transform(BiFunction<CardContext, CardFile, Iterator<CardQueryRequest>> transformer) {
      return (cardContext) -> {
        CardFile cardFile = this.get(cardContext);
        return transformer.apply(cardContext, cardFile);
      };
    }
  }


  @FunctionalInterface
  interface CardTransformer {

    Iterator<CardQueryRequest> get(CardContext cardContext);

    default CardQueryExecutor process(Function<CardQueryRequest, CardQueryResponse> cardQueryRequestConsumer) {
      return cardContext -> {
        Iterator<CardQueryRequest> cardQueryRequestIterator = this.get(cardContext);
        List<CardQueryResponse> collector = new ArrayList<>();
        CardMetadata cardMetadata = null;
        while (cardQueryRequestIterator.hasNext()) {
          CardQueryRequest cardQueryRequest = cardQueryRequestIterator.next();
          if (cardMetadata == null) {
            cardMetadata = cardQueryRequest.metadata();
          }
          CardQueryResponse carQueryResponse = cardQueryRequestConsumer.apply(cardQueryRequest);
          collector.add(carQueryResponse);
        }

        return new CardResponse(cardMetadata, collector);
      };
    }

  }

  @FunctionalInterface
  interface CardQueryExecutor {

    CardResponse onCardContext(CardContext cardContext);

    default CardRunner run(CardReportWriter reportWriter, CardExecutionWriter executionWriter) {
      return cardContext -> {
        CardResponse cardResponse = this.onCardContext(cardContext);
        executionWriter.writeCardExecution(cardResponse.metadata(), cardContext.parameters());
        List<CardQueryResponse> cardQueryResponses = cardResponse.responses();
        for (CardQueryResponse cardQueryResponse : cardQueryResponses) {
          try {
            reportWriter.onNext(cardQueryResponse);
          } catch (Exception e) {
            reportWriter.onError(e);
          }
        }
        reportWriter.onComplete();
      };
    }
  }

  public record CardResponse(CardMetadata metadata, List<CardQueryResponse> responses) {
  }
}

