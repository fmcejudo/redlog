package com.github.fmcejudo.redlogs.engine.card;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class CardExecutionService implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(CardExecutionService.class);

    private final CardTaskRunnerBuilder cardTaskRunnerBuilder;

    private final ExecutorService executor;

    public CardExecutionService(final LokiClient lokiClient) {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.cardTaskRunnerBuilder = CardTaskRunner.create(new LokiCardTask(lokiClient), executor);
    }

    public void execute(final String applicationName) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //process
        Card card = CardEnum.getCardByName(applicationName);
        CardReportWriter writer = new CardReportWriter();
        cardTaskRunnerBuilder.onSuccess(writer::printReport).runTask(card);

        //end process
        stopWatch.stop();
        logger.warn("time to execute method: {} in thread: {}",
                stopWatch.prettyPrint(TimeUnit.MILLISECONDS), Thread.currentThread().getName()
        );
    }

    @Override
    public void close() throws IOException {
        executor.shutdown();
        executor.close();
    }
}

@FunctionalInterface
interface CardTaskRunner {

    void runTask(Card card);

    static CardTaskRunnerBuilder create(final CardTask cardTask, final Executor executor){
        return onSuccess -> {
            return card -> {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (CardQuery cardQuery : card.cardQueries()) {
                    CompletableFuture<Void> future = CompletableFuture
                            .supplyAsync(() -> cardTask.result(cardQuery), executor)
                            .thenApply(SuccessTaskResult::new)
                            .thenAccept(onSuccess);
                    futures.add(future);
                }

                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            };
        };
    }
}

@FunctionalInterface
interface CardTaskRunnerBuilder {
    
    CardTaskRunner onSuccess(Consumer<CardTaskResult> cardTaskResultConsumer);
}
