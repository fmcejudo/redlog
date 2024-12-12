package com.github.fmcejudo.redlogs.card;

import java.io.Closeable;

import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessorFactory;
import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.processor.ProcessorContext;
import io.github.fmcejudo.redlogs.card.processor.filter.ResponseEntryFilter;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class CardRunner implements Closeable {

  private final Logger logger = LoggerFactory.getLogger(CardRunner.class);

  private final CardLoader cardLoader;

  private final CardProcessorFactory processorFactory;

  private final CardExecutionWriter executionWriter;

  private final CardReportWriter reportWriter;

  public CardRunner(final CardLoader cardLoader,
      final CardProcessorFactory processorFactory,
      final CardExecutionWriter executionWriter,
      final CardReportWriter repotWriter) {

    this.cardLoader = cardLoader;
    this.processorFactory = processorFactory;
    this.executionWriter = executionWriter;
    this.reportWriter = repotWriter;
  }

  public void run(final CardContext cardContext) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    CardQueryExecution.withProvider(() -> cardLoader.load(cardContext))
        .withProcessors(processorFactory)
        .executeAndWriteTo(executionWriter, reportWriter);

    stopWatch.stop();
    long totalTimeMillis = stopWatch.getTotalTimeMillis();
    logger.info("Processing card {} took {} ms", cardContext.applicationName(), totalTimeMillis);
  }

  @Override
  public void close() {
    processorFactory.close();
  }
}

@FunctionalInterface
interface CardQueryProvider {

  CardRequest provide();

  default CardQueryExecution withProcessors(final CardProcessorFactory processorFactory) {
    return (cardExecutionWriter, cardResponseWriter) -> {
      CardRequest cardRequest = this.provide();
      String executionId = cardExecutionWriter.writeCardExecution(cardRequest);

      for (CardQueryRequest cardQueryRequest : cardRequest.cardQueryRequests()) {
        processCardQueryRequest(processorFactory, cardResponseWriter, executionId, cardQueryRequest, cardRequest);
      }
    };
  }

  private void processCardQueryRequest(final CardProcessorFactory processorFactory,
      final CardReportWriter cardResponseWriter, final String executionId, final CardQueryRequest cqr, final CardRequest cr) {
    ProcessorContext processorContext = new ProcessorContext(executionId, cqr, cr.startTime(), cr.endTime());
    processorFactory.ofType(cqr.type()).process(processorContext, ResponseEntryFilter.getInstance(cqr), cardResponseWriter);
  }

}

@FunctionalInterface
interface CardQueryExecution {

  void executeAndWriteTo(CardExecutionWriter executionWriter, CardReportWriter reportWriter);

  static CardQueryProvider withProvider(CardQueryProvider cardQueryProvider) {
    return cardQueryProvider;
  }
}







