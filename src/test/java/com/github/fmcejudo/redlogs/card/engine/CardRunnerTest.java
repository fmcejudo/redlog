package com.github.fmcejudo.redlogs.card.engine;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.card.engine.model.CardType;
import com.github.fmcejudo.redlogs.card.engine.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.engine.writer.CardResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CardRunnerTest {

    @Test
    void shouldCreateAnExecution() {
        //Given
        String applicationName = "TEST";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of("date", LocalDate.now().format(ISO_LOCAL_DATE)));

        CardLoader executionLoader = new TestCardLoader();
        CardProcessor cardProcessor = new TestCardProcessor(r -> r);
        CardResponseWriter assertWriter = new TestCardWriter(result -> {
            Assertions.assertThat(result).satisfies(r -> {
                Assertions.assertThat(r.applicationName()).isEqualTo(applicationName);
                Assertions.assertThat(r.id()).isEqualTo("test");
                Assertions.assertThat(r.executionId()).isNotNull();
            });
        });
        RedlogExecutionService redlogExecutionService = mock(RedlogExecutionService.class);
        Mockito.doNothing().when(redlogExecutionService).saveOrReplaceExecution(anyString(), any(CardContext.class));
        Mockito.doAnswer(a -> {
            String status = a.getArgument(1);
            Assertions.assertThat(status).isEqualTo("SUCCESS");
            return null;
        }).when(redlogExecutionService).updateExecution(anyString(), anyString());

        try (CardRunner cardExecutionService = new CardRunner(
                executionLoader, cardProcessor, assertWriter, redlogExecutionService
        )) {
            //When && Then
            cardExecutionService.run(cardExecutionContext);
        }

        verify(redlogExecutionService, times(1)).saveOrReplaceExecution(anyString(), any(CardContext.class));
        verify(redlogExecutionService, times(1)).updateExecution(anyString(), anyString());
    }

    @Test
    void shouldFailOnAnExecution() {
        //Given
        String applicationName = "TEST";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of("date", LocalDate.now().format(ISO_LOCAL_DATE)));

        CardLoader executionLoader = new TestCardLoader();
        CardProcessor cardProcessor = new TestCardProcessor(r -> {
            throw new RuntimeException("error");
        });
        CardResponseWriter assertWriter = new TestCardWriter(result -> {
        });
        RedlogExecutionService redlogExecutionService = mock(RedlogExecutionService.class);
        Mockito.doNothing().when(redlogExecutionService).saveOrReplaceExecution(anyString(), any(CardContext.class));
        Mockito.doAnswer(a -> {
            String status = a.getArgument(1);
            Assertions.assertThat(status).isEqualTo("ERROR");
            return null;
        }).when(redlogExecutionService).updateExecution(anyString(), anyString());

        try (CardRunner cardExecutionService = new CardRunner(
                executionLoader, cardProcessor, assertWriter, redlogExecutionService
        )){
            //When && Then
            Assertions.assertThatThrownBy(() -> cardExecutionService.run(cardExecutionContext))
                    .isInstanceOf(RuntimeException.class).hasMessageContaining("error");
        }
    }

}

class TestCardLoader implements CardLoader {

    @Override
    public List<CardQueryRequest> load(final CardContext cardExecutionContext) {

        String application = cardExecutionContext.applicationName();
        var cardQueryRequest = new CardQueryRequest(application, "test", "section test", CardType.SUMMARY, "query")
                .withExecutionId(UUID.randomUUID().toString());
        return List.of(cardQueryRequest);
    }
}

record TestCardProcessor(Function<CardQueryResponse, CardQueryResponse> transformResponse) implements CardProcessor {

    @Override
    public CardQueryResponse process(CardQueryRequest cardQuery) {
        String appName = cardQuery.applicationName();
        String id = cardQuery.id();
        String description = cardQuery.description();
        String executionId = cardQuery.executionId();

        CardQueryResponseEntry responseEntry = new CardQueryResponseEntry(Map.of("test", "test"), 1L);
        CardQueryResponse response = new CardQueryResponse(
                appName, LocalDate.now(), id, executionId, description, List.of(responseEntry), "", null
        );
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
