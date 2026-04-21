package com.github.fmcejudo.redlogs.card.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import io.github.fmcejudo.redlogs.card.CardQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLMapper;

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
  void shouldBeInvalidOnEmptyQueries() {

    //Given && When
    Exception exception = Assertions.catchException(() -> new CardFile(List.of(), LocalTime.now(), "24h", List.of()));

    //Then
    assertThat(exception).isInstanceOf(CardExecutionException.class).hasMessageContaining("it can't run a report template with no queries");
  }

}