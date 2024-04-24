package com.github.fmcejudo.redlogs.service;

import com.github.fmcejudo.redlogs.engine.card.CardExecutionService;
import com.github.fmcejudo.redlogs.engine.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.engine.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.engine.card.writer.CardResponseMongoWriter;
import com.github.fmcejudo.redlogs.engine.card.writer.CardResponseWriter;
import com.github.fmcejudo.redlogs.writer.ReportWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class RedLogScheduler {

    private final ReportWriter reportWriter;

    private final CardProcessor processor;

    private final CardResponseWriter cardResponseWriter;

    @Value("${github.token}")
    private String githubToken;

    public RedLogScheduler(CardProcessor processor, MongoTemplate mongoTemplate, ReportWriter reportWriter) {
        this.processor = processor;
        this.reportWriter = reportWriter;
        this.cardResponseWriter = new CardResponseMongoWriter(mongoTemplate);
    }

    @Scheduled(cron = "0 0 6 * * *")
    @Async
    public void execute() {
        this.execute("ALERTHUB");
    }

    public void execute(String applicationName) {

        try (CardExecutionService service =
                     new CardExecutionService(CardLoader.getGithubLoader(githubToken), processor, cardResponseWriter)) {
            service.execute(applicationName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println("execute tasks to extract logs");
    }
}
