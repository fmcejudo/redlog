package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.github.fmcejudo.redlogs.util.MongoNamingUtils.composeCollectionName;

public interface CardResponseWriter {

    void writeExecution(CardRequest cardRequest);

    void onNext(CardQueryResponse cardTaskResult);

    void onError(Throwable throwable);

    void onComplete();
}

class MongoCardResponseWriter implements CardResponseWriter {


    private static final Logger log = LoggerFactory.getLogger(MongoCardResponseWriter.class);
    private final MongoTemplate mongoTemplate;
    private final String reportCollectionName;
    private final String executionCollectionName;

    public MongoCardResponseWriter(final MongoTemplate mongoTemplate,
                                   final RedLogMongoProperties redLogMongoConfigProperties) {
        this.mongoTemplate = mongoTemplate;
        String mongoPrefix = redLogMongoConfigProperties.getCollectionNamePrefix();
        this.reportCollectionName = composeCollectionName(mongoPrefix, "reports");
        this.executionCollectionName = composeCollectionName(mongoPrefix, "executions");
    }


    @Override
    public void onNext(CardQueryResponse response) {
        log.info("next: {}", response);

        Map<String, Object> reportObject = new HashMap<>(Map.of(
                "_id", String.join(".", response.id(), response.executionId()),
                "reportId", response.id(),
                "executionId", response.executionId(),
                "description", response.description(),
                "link", response.link(),
                "items", response.currentEntries()
        ));
        mongoTemplate.save(reportObject, reportCollectionName);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("error: {}", throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("completed");
    }

    @Override
    public void writeExecution(final CardRequest cardRequest) {
        log.info("it starts execution {}", cardRequest.executionId());
        Map<String, Object> execution = new HashMap<>(Map.of(
                "_id", cardRequest.executionId(),
                "applicationName", cardRequest.applicationName(),
                "parameters", Map.of(),
                "reportDate", cardRequest.endTime().toLocalDate()
        ));
        mongoTemplate.save(execution, executionCollectionName);

    }
}
