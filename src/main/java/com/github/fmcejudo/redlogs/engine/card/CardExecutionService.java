package com.github.fmcejudo.redlogs.engine.card;

import com.github.fmcejudo.redlogs.engine.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.engine.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.engine.card.writer.CardResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CardExecutionService implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(CardExecutionService.class);

    private final ExecutorService executor;

    private final CardLoader cardLoader;
    private final CardProcessor processor;
    private final CardResponseWriter writer;

    public CardExecutionService(final CardLoader cardLoader,
                                final CardProcessor processor,
                                final CardResponseWriter writer) {

        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.cardLoader = cardLoader;
        this.processor = processor;
        this.writer = writer;
    }

    public void execute(final String applicationName) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final Instant start = Instant.now();
        //process
        CardQueryExecution.withProvider(() -> cardLoader.load(applicationName))
                .withProcessor(processor, executor)
                .execute(response -> {
                    CardQueryResponse write = writer.write(response);
                    System.out.println(write.id() + " took " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms");
                }, () -> System.out.println("complete"));

        //end process
        stopWatch.stop();
        logger.warn("time to execute method: {} in thread: {}",
                stopWatch.prettyPrint(), Thread.currentThread().getName()
        );
    }

    @Override
    public void close() {
        executor.shutdown();
        executor.close();
    }
}

@FunctionalInterface
interface CardQueryExecution {

    void execute(Consumer<CardQueryResponse> onNext, Runnable onComplete);

    static CardQueryProvider withProvider(Supplier<List<CardQueryRequest>> queryRequestProvider) {
        return queryRequestProvider::get;
    }
}

@FunctionalInterface
interface CardQueryProvider {

    List<CardQueryRequest> provide();

    default CardQueryExecution withProcessor(CardProcessor processor, Executor executor) {
        return (onNext, onComplete) -> {
            List<CardQueryRequest> queryRequests = this.provide();
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (CardQueryRequest cardQueryRequest : queryRequests) {
                CompletableFuture<Void> future = CompletableFuture
                        .supplyAsync(() -> processor.process(cardQueryRequest), executor)
                        .thenAccept(onNext);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            onComplete.run();
        };
    }
}
