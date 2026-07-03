package io.github.fmcejudo.redlogs.loki.processor;

import java.time.LocalDateTime;

import io.github.fmcejudo.redlogs.loki.processor.LokiLinkBuilder.QueryPart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LokiLinkBuilderTest {

  @Test
  void shouldCreateALokiLinkWithQueries() {
    //Given
    final String query = """
        {filter="something"} | regexp `"(?P<def>.*)"`""";

    String encodedQuery =
        """
           queries%22:%5B%7B%22refId%22:%22A%22,%22expr%22:%22%7Bfilter%3D%5C%22something%5C%22%7D%20%7C%20\
           regexp%20%60%5C%22%28%3FP%3Cdef%3E.%2A%29%5C%22%60%22\
           """;

    //When
    String lokiLink = LokiLinkBuilder.builder("http://loki.io", "Loki")
        .query(query)
        .from(LocalDateTime.now().minusHours(1))
        .to(LocalDateTime.now())
        .build();

    //Then
    Assertions.assertThat(lokiLink).contains("http://loki.io").contains(encodedQuery);
  }

  @Test
  void shouldEncodeQuery() {
    // Given
    final String query = """
        {filter="something"} | regexp `"(?P<def>.*)"`""";

    QueryPart queryPart = QueryPart.create("A", query, "loki");

    // When
    String expr = queryPart.expr();

    // Then
    Assertions.assertThat(expr).isEqualTo(query);
  }

  @Test
  void shouldBuildQueryPart() {
    // Given
    final String query = """
        {filter="something"} | regexp `"(?P<def>.*)"`""";

    final String queryPartJson =
        "{\"refId\":\"A\",\"expr\":\"{filter=\\\"something\\\"} | regexp `\\\"(?P<def>.*)\\\"`\",\"queryType\":\"range\","
        + "\"datasource\":{\"type\":\"loki\",\"uid\":\"Loki\"},\"editorMode\":\"code\",\"direction\":\"backward\"}";

    QueryPart queryPart = QueryPart.create("A", query, "Loki");

    // When
    String result = queryPart.toString();

    // Then
    Assertions.assertThat(result).isEqualTo(queryPartJson);
  }

  @Test
  void shouldEncodeEspecialCharacters() {
    // Given
    final String query = """
        \n\r'`~?.*{}[]()|$+<>""";

    // When
    String encodedQuery =
        """
          %22%5Cn%5Cr%27%60~%3F.%2A%7B%7D%5B%5D%28%29%7C$%2B%3C%3E%22""";

    //When
    String lokiLink = LokiLinkBuilder.builder("http://loki.io", "Loki")
        .query(query)
        .from(LocalDateTime.now().minusHours(1))
        .to(LocalDateTime.now())
        .build();

    //Then
    Assertions.assertThat(lokiLink).contains("http://loki.io").contains(encodedQuery);
  }

}
