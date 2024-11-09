package com.github.fmcejudo.redlogs.card;

import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.Closeable;

public class CardRunner implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(CardRunner.class);

    private final CardLoader cardLoader;
    private final CardProcessor processor;
    private final CardResponseWriter writer;

    public CardRunner(final CardLoader cardLoader,
                      final CardProcessor processor,
                      final CardResponseWriter writer) {

        this.cardLoader = cardLoader;
        this.processor = processor;
        this.writer = writer;
    }

    public void run(final CardContext cardContext) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CardQueryExecution.withProvider(() -> cardLoader.load(cardContext))
                .withProcessor(processor)
                .executeAndWriteTo(writer);

        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        logger.info("Processing card {} took {} ms" , cardContext.applicationName(), totalTimeMillis);
    }

    @Override
    public void close() {
        processor.close();
    }
}

@FunctionalInterface
interface CardQueryProvider {

    CardRequest provide();

    default CardQueryExecution withProcessor(CardProcessor processor) {
        return (cardResponseWriter) -> {
            CardRequest cardRequest = this.provide();
            processor.process(cardRequest, cardResponseWriter);
        };
    }

}

@FunctionalInterface
interface CardQueryExecution {

    void executeAndWriteTo(CardResponseWriter writer);

    static CardQueryProvider withProvider(CardQueryProvider cardQueryProvider) {
        return cardQueryProvider;
    }
}







