package com.github.fmcejudo.redlogs.card;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import static io.github.fmcejudo.redlogs.card.domain.CardType.SUMMARY;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessorFactory;
import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.domain.CardQueryRequest.CardQueryContext;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.processor.ProcessorContext;
import io.github.fmcejudo.redlogs.card.processor.filter.ResponseEntryFilter;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CardRunnerTest {

  private CardProcessorFactory cardProcessorFactory;

  @BeforeEach
  void setUp() {
    this.cardProcessorFactory = Mockito.mock(CardProcessorFactory.class);
    Mockito.when(cardProcessorFactory.ofType(anyString())).thenReturn(new TestCardProcessor(CardReportWriter::onNext));
  }

  @Test
  void shouldCreateAnExecution() {
    //Given
    String applicationName = "TEST";
    var cardExecutionContext =
        CardContext.from(applicationName, Map.of("date", LocalDate.now().format(ISO_LOCAL_DATE)));

    CardLoader executionLoader = new TestCardLoader();
    CardExecutionWriter assertExecutionWriter = new TestExecutionWriter();
    CardReportWriter assertReportWriter = new TestCardWriter(result ->
        Assertions.assertThat(result).satisfies(r -> {
          Assertions.assertThat(r.id()).isEqualTo("test");
          Assertions.assertThat(r.executionId()).isNotNull();
        }),
        throwable -> Assertions.fail("No error was expected on processing card")
    );

    try (CardRunner cardRunner = new CardRunner(executionLoader, cardProcessorFactory, assertExecutionWriter, assertReportWriter)) {
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
    CardExecutionWriter assertExecutionWriter = new TestExecutionWriter();
    CardReportWriter assertReportWriter = new TestCardWriter(
        result -> {
        },
        throwable -> Assertions.assertThat(throwable).isInstanceOf(RuntimeException.class).hasMessageContaining("error")
    );

    try (CardRunner cardRunner = new CardRunner(executionLoader, cardProcessorFactory, assertExecutionWriter, assertReportWriter)) {
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
        .getInstance(SUMMARY, new CardQueryContext("test", "LOKI", "section test", "query"))
        .withExecutionId(executionId);

    return new CardRequest(
        application, cardContext.reportDate(), null, null, List.of(cardQueryRequest), Map.of()
    ).withExecutionId(executionId);
  }
}

record TestCardProcessor(BiConsumer<CardReportWriter, CardQueryResponse> responseConsumer) implements CardProcessor {

  @Override
  public void process(ProcessorContext processorContext, ResponseEntryFilter filters, CardReportWriter writer) {

    String id = processorContext.id();
    String description = processorContext.description();
    CardQueryResponseEntry responseEntry = new CardQueryResponseEntry(Map.of("test", "test"), 1L);
    CardQueryResponse response = new CardQueryResponse(
        LocalDate.now(), id, processorContext.executionId(), description, List.of(responseEntry), "", null
    );
    responseConsumer.accept(writer, response);

    writer.onComplete();
  }
}

final class TestCardWriter implements CardReportWriter {

  private final Consumer<CardQueryResponse> assertResponseConsumer;

  private final Consumer<Throwable> assertThrowableConsume;

  TestCardWriter(Consumer<CardQueryResponse> assertResponseConsumer, Consumer<Throwable> assertThrowableConsume) {
    this.assertResponseConsumer = assertResponseConsumer;
    this.assertThrowableConsume = assertThrowableConsume;
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

final class TestExecutionWriter implements CardExecutionWriter {

  @Override
  public String writeCardExecution(CardRequest cardRequest) {
    return UUID.randomUUID().toString();
  }
}
