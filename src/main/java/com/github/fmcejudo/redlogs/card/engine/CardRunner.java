package com.github.fmcejudo.redlogs.card.engine;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.engine.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.engine.writer.CardResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CardRunner implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(CardRunner.class);

    private final ExecutorService executor;

    private final CardLoader cardLoader;
    private final CardProcessor processor;
    private final CardResponseWriter writer;
    private final RedlogExecutionService redlogExecutionService;

    public CardRunner(final CardLoader cardLoader,
                      final CardProcessor processor,
                      final CardResponseWriter writer,
                      final RedlogExecutionService redlogExecutionService) {

        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.cardLoader = cardLoader;
        this.processor = processor;
        this.writer = writer;
        this.redlogExecutionService = redlogExecutionService;
    }

    public void run(final CardContext cardContext) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String uuid = UUID.randomUUID().toString();
        CardQueryExecution.withProvider(() -> cardLoader.load(cardContext))
                .onCardRequestsReady(() -> {
                    redlogExecutionService.saveOrReplaceExecution(uuid, cardContext);
                    return uuid;
                })
                .withProcessor(processor, executor)
                .execute(writer::write,
                        t -> {
                            redlogExecutionService.updateExecution(uuid, "ERROR");
                            throw new RuntimeException(t);
                        },
                        () -> {
                            stopWatch.stop();
                            redlogExecutionService.updateExecution(uuid, "SUCCESS");
                            logger.warn("time to execute method: {}", stopWatch.prettyPrint());
                        }
                );

    }

    @Override
    public void close() {
        executor.shutdown();
        executor.close();
    }
}

@FunctionalInterface
interface CardQueryProvider {

    List<CardQueryRequest> provide();

    default CardQueryProvider onCardRequestsReady(final ExecutionRegistration executionRegistration) {
        return () -> {
            List<CardQueryRequest> cardQueryRequests = this.provide();
            String uuid = executionRegistration.run();
            return cardQueryRequests.stream().map(cqr -> cqr.withExecutionId(uuid)).toList();
        };
    }

    default CardQueryExecution withProcessor(CardProcessor processor, Executor executor) {
        return (onNext, onError, onComplete) -> {
            List<CardQueryRequest> queryRequests = this.provide();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (CardQueryRequest cardQueryRequest : queryRequests) {
                var future = evaluateCardRequest(processor, executor, onNext, onError, cardQueryRequest);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            onComplete.run();
        };
    }

    private CompletableFuture<Void> evaluateCardRequest(CardProcessor processor, Executor executor,
                                                        Consumer<CardQueryResponse> onNext, Consumer<Throwable> onError,
                                                        CardQueryRequest cardQueryRequest) {
        return CompletableFuture
                .supplyAsync(() -> processor.process(cardQueryRequest), executor)
                .thenAcceptAsync(onNext, executor)
                .exceptionally(t -> {
                    onError.accept(t);
                    return null;
                });
    }
}

@FunctionalInterface
interface CardQueryExecution {

    void execute(Consumer<CardQueryResponse> onNext, Consumer<Throwable> onError, Runnable onComplete);

    static CardQueryProvider withProvider(CardQueryProvider cardQueryProvider) {
        return cardQueryProvider;
    }
}

@FunctionalInterface
interface ExecutionRegistration extends Supplier<String> {

    default String run() {
        return this.get();
    }

}







