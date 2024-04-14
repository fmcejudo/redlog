package com.github.fmcejudo.redlogs.service;

import com.github.fmcejudo.redlogs.engine.card.CardExecutionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedLogScheduler {

    private final CardExecutionService cardExecutionService;

    public RedLogScheduler(final CardExecutionService cardExecutionService) {
        this.cardExecutionService = cardExecutionService;
    }

    @Scheduled(cron = "*/30 * * * * *")
    @Async
    public void execute() {
        cardExecutionService.execute("ALERTHUB");
        System.out.println("execute tasks to extract logs");
    }
}
