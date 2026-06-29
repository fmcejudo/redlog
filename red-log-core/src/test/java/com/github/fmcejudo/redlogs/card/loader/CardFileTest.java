package com.github.fmcejudo.redlogs.card.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.runner.CardRunner;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardFileTest {

  YAMLMapper mapper;

  @BeforeEach
  void setUp() {
    SimpleModule cardQueryModule = new SimpleModule();
    cardQueryModule.addDeserializer(CardQuery.class, new CardQueryDeserializer());
    this.mapper = YAMLMapper.builder()
        .addModule(cardQueryModule)
        .build();
  }

  @Test
  void shouldDeserializeCardFile() throws IOException {
    // Given
    String yamlContent = """
        queries:
          - id: db_connection_problems
            description: DB Connection Issues
            type: COUNT
            expectedAtLeast: 3
            query: |
              {app="my-app", service="my-service", level="ERROR"}
              |~ `Timeout Exception
        """;

    // When
    CardFile cardFile = mapper.readValue(yamlContent, CardFile.class);

    // Then
    Assertions.assertThat(cardFile).isNotNull();
    Assertions.assertThat(cardFile.queries()).hasSize(1).first().satisfies(q -> {
      Assertions.assertThat(q.id()).isEqualTo("db_connection_problems");
      Assertions.assertThat(q.processor()).isNull();
    });
  }

  @Test
  void shouldBeInvalidWhenRunnerReceivesEmptyQueries() {
    // Given
    CardFileLoader emptyLoader = ctx -> new CardFile(List.of(), LocalTime.now(), "24h", List.of());
    CardReportWriter noopWriter = new CardReportWriter() {
      public void onNext(CardQueryResponse r) {}
      public void onError(Throwable t) {}
      public void onComplete() {}
    };

    // When
    Exception exception = Assertions.catchException(() ->
        CardRunner.load(emptyLoader)
            .transform((cc, cf) -> Collections.emptyIterator())
            .process(cqr -> null)
            .run(noopWriter, new TestExecutionWriter())
            .onCardContext(CardContext.from("TEST", Map.of()))
    );

    // Then
    assertThat(exception)
        .isInstanceOf(CardExecutionException.class)
        .hasMessageContaining("it can't run a report template with no queries");
  }

}

class TestExecutionWriter implements io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter {
  @Override
  public String writeCardExecution(io.github.fmcejudo.redlogs.card.CardMetadata cardMetadata, Map<String, String> parameters) {
    return cardMetadata.executionId();
  }
}
