package com.github.fmcejudo.redlogs.engine.card;

import com.github.fmcejudo.redlogs.engine.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.engine.card.model.CardType;
import com.github.fmcejudo.redlogs.engine.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.engine.card.writer.CardResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

class CardExecutionServiceTest {


    @Test
    void shouldCreateAnExecution() {
        //Given
        final String applicationName = "TEST";

        CardLoader executionLoader = new TestCardLoader();
        CardProcessor cardProcessor = new TestCardProcessor(r -> r);
        CardResponseWriter assertWriter = new TestCardWriter(result -> {
            Assertions.assertThat(result).satisfies(r -> {
                Assertions.assertThat(r.applicationName()).isEqualTo(applicationName);
                Assertions.assertThat(r.id()).isEqualTo("test");
            });
        });

        try (CardExecutionService cardExecutionService = new CardExecutionService(
                executionLoader, cardProcessor, assertWriter
        )){
            //When && Then
            cardExecutionService.execute(applicationName, LocalDate.now());
        }
    }

    @Test
    void shouldFailOnAnExecution() {
        //Given
        final String applicationName = "TEST";

        CardLoader executionLoader = new TestCardLoader();
        CardProcessor cardProcessor = new TestCardProcessor(r -> {
            throw new RuntimeException("error");
        });
        CardResponseWriter assertWriter = new TestCardWriter(result -> {
        });


        try (CardExecutionService cardExecutionService = new CardExecutionService(
                executionLoader, cardProcessor, assertWriter
        )){
            //When && Then
            Assertions.assertThatThrownBy(() -> cardExecutionService.execute(applicationName, LocalDate.now()))
                    .isInstanceOf(RuntimeException.class).hasMessageContaining("error");
        }
    }

}

class TestCardLoader implements CardLoader {

    @Override
    public List<CardQueryRequest> load(String application, LocalDate reportDate) {
        var cardQueryRequest = new CardQueryRequest(application, "test", "section test", CardType.SUMMARY, "query");
        return List.of(cardQueryRequest);
    }
}

class TestCardProcessor implements CardProcessor {

    public final Function<CardQueryResponse, CardQueryResponse> transformResponse;

    TestCardProcessor(Function<CardQueryResponse, CardQueryResponse> transformResponse) {
        this.transformResponse = transformResponse;
    }

    @Override
    public CardQueryResponse process(CardQueryRequest cardQuery) {
        String appName = cardQuery.applicationName();
        String id = cardQuery.id();
        String description = cardQuery.description();

        CardQueryResponseEntry responseEntry = new CardQueryResponseEntry(Map.of("test", "test"), 1L);
        CardQueryResponse response =
                new CardQueryResponse(appName, LocalDate.now(), id, description, List.of(responseEntry), "", null);
        return transformResponse.apply(response);
    }
}

record TestCardWriter(Consumer<CardQueryResponse> assertResponseConsumer) implements CardResponseWriter {

    @Override
    public CardQueryResponse write(CardQueryResponse cardTaskResult) {
        assertResponseConsumer.accept(cardTaskResult);
        return cardTaskResult;
    }
}