package com.github.fmcejudo.redlogs.card;

import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest.CardQueryContext;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.github.fmcejudo.redlogs.card.model.CardType.SUMMARY;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

class CardRunnerTest {

    @Test
    void shouldCreateAnExecution() {
        //Given
        String applicationName = "TEST";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of("date", LocalDate.now().format(ISO_LOCAL_DATE)));

        CardLoader executionLoader = new TestCardLoader();
        CardProcessor cardProcessor = new TestCardProcessor(CardResponseWriter::onNext);
        CardResponseWriter assertWriter = new TestCardWriter(result -> {
            Assertions.assertThat(result).satisfies(r -> {
                Assertions.assertThat(r.applicationName()).isEqualTo(applicationName);
                Assertions.assertThat(r.id()).isEqualTo("test");
                Assertions.assertThat(r.executionId()).isNotNull();
            });
        }, throwable -> {
            Assertions.fail("No error was expected on processing card");
        });
        try (CardRunner cardRunner = new CardRunner(executionLoader, cardProcessor, assertWriter)) {
            //When && Then
            cardRunner.run(cardExecutionContext);
        }

    }

    @Test
    void shouldFailOnAnExecution() {
        //Given
        String applicationName = "TEST";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of("date", LocalDate.now().format(ISO_LOCAL_DATE)));

        CardLoader executionLoader = new TestCardLoader();
        CardProcessor cardProcessor = new TestCardProcessor((w, r) -> w.onError(new RuntimeException("error")));
        CardResponseWriter assertWriter = new TestCardWriter(result -> {
        }, throwable -> {
            Assertions.assertThat(throwable).isInstanceOf(RuntimeException.class).hasMessageContaining("error");
        });

        try (CardRunner cardRunner = new CardRunner(executionLoader, cardProcessor, assertWriter)) {
            //When && Then
            cardRunner.run(cardExecutionContext);
        }
    }

}

class TestCardLoader implements CardLoader {

    @Override
    public CardRequest load(final CardContext cardContext) {

        String executionId = UUID.randomUUID().toString();

        String application = cardContext.applicationName();
        CardQueryRequest cardQueryRequest = CardQueryRequest
                .getInstance(SUMMARY, new CardQueryContext("test", "section test", "query"))
                .withExecutionId(executionId);

        return new CardRequest(
                application, cardContext.reportDate(), null, null, List.of(cardQueryRequest), Map.of()
        ).withExecutionId(executionId);
    }
}

record TestCardProcessor(BiConsumer<CardResponseWriter, CardQueryResponse> responseConsumer) implements CardProcessor {

    @Override
    public void process(CardRequest cardRequest, CardResponseWriter writer) {
        String appName = cardRequest.applicationName();
        String executionId = cardRequest.executionId();

        cardRequest.cardQueryRequests().forEach(cqr -> {
            String id = cqr.id();
            String description = cqr.description();
            CardQueryResponseEntry responseEntry = new CardQueryResponseEntry(Map.of("test", "test"), 1L);
            CardQueryResponse response = new CardQueryResponse(
                    appName, LocalDate.now(), id, executionId, description, List.of(responseEntry), "", null
            );
            responseConsumer.accept(writer, response);
        });

        writer.onComplete();
    }
}

final class TestCardWriter implements CardResponseWriter {


    private final Consumer<CardQueryResponse> assertResponseConsumer;
    private final Consumer<Throwable> assertThrowableConsume;

    TestCardWriter(Consumer<CardQueryResponse> assertResponseConsumer, Consumer<Throwable> assertThrowableConsume) {
        this.assertResponseConsumer = assertResponseConsumer;
        this.assertThrowableConsume = assertThrowableConsume;
    }

    @Override
    public void writeExecution(CardRequest cardRequest) {

    }

    @Override
    public void onNext(CardQueryResponse cardTaskResult) {
        assertResponseConsumer.accept(cardTaskResult);
    }

    @Override
    public void onError(Throwable throwable) {
        assertThrowableConsume.accept(throwable);
    }

    @Override
    public void onComplete() {

    }
}
