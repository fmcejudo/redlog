package com.github.fmcejudo.redlogs.engine.card;

import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;
import com.github.fmcejudo.redlogs.client.loki.LokiRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;

@FunctionalInterface
interface CardTask {

    CardReportEntries result(CardQuery cardQuery);

}

@Component
class LokiCardTask implements CardTask {

    private final LokiClient lokiClient;

    LokiCardTask(final LokiClient lokiClient) {
        this.lokiClient = lokiClient;
    }

    public CardReportEntries result(final CardQuery cardQuery) {
        String query = cardQuery.query();

        var type = switch (cardQuery.cardType()) {
            case SERVICE -> LokiRequest.RequestType.POINT_IN_TIME;
            case COUNT -> LokiRequest.RequestType.INSTANT;
        };

        LokiResponse lokiResponse = lokiClient.query(new LokiRequest(type, query));
        return composeResult(cardQuery, lokiResponse);
    }

    private CardReportEntries composeResult(final CardQuery cardQuery, final LokiResponse lokiResponse) {
        String id = cardQuery.id();
        String description = cardQuery.description();
        if (lokiResponse == null) {
            return CardReportEntries.failed(id, description, List.of(new CardException("No report response found")));
        }

        if (lokiResponse.isSuccess()) {
            return buildCardReportEntries(cardQuery, lokiResponse);
        }
        return CardReportEntries.failed(id, description, List.of(new CardException("query ended up being failed")));
    }

    private CardReportEntries buildCardReportEntries(CardQuery cardQuery, LokiResponse lokiResponse) {
        String id = cardQuery.id();
        String description = cardQuery.description();
        List<CardReportEntry> entries = lokiResponse.result().stream()
                .map(result -> new CardReportEntry(
                        cardQuery.id(), cardQuery.description(), result.labels(), result.count()
                ))
                .toList();
        return CardReportEntries.success(id, description, entries);
    }
}
