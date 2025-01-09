package io.github.fmcejudo.redlogs.loki.processor;

import io.github.fmcejudo.redlogs.loki.processor.LokiLinkBuilder.QueryPart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryPartTest {

  @Test
  void shouldPrepareQuery() {
    //Given
    final String query = """
        sum by(reporter,name) (count_over_time(
          {app="event-tracker", level="WARN"}
          |~ `[expense]`
          | json | line_format `{{.message}}`[24h]
        ))""";

    final String expectedEncodedQuery = """
        sum by(reporter,name) (count_over_time(
          {app=\\"event-tracker\\", level=\\"WARN\\"}
          |~ `[expense]`
          | json | line_format `{{.message}}`[24h]
        ))""";

    //When
    QueryPart queryPart = QueryPart.create("A", query, "default");

    //Then
    Assertions.assertThat(queryPart.refId()).isEqualTo("A");
    Assertions.assertThat(queryPart.expr()).isEqualTo(expectedEncodedQuery);
  }

  @Test
  void shouldEncodeFromLokiLinkBuilder() {
    //Given

    final String query = """
        sum by(reporter,name) (count_over_time(
          {app="event-tracker", level="WARN"}
          |~ `[expense]`
          | json | line_format `{{.message}}`[24h]
        ))""";

    final String expectedEncodedQuery = """
        sum%20by%28reporter,name%29%20%28count_over_time%28%5Cn%20%20%7Bapp%3D%5C%22event-tracker%5C%22,\
        %20level%3D%5C%22WARN%5C%22%7D%5Cn%20%20%7C~%20%60%5Bexpense%5D%60%5Cn%20%20%7C%20json%20%7C%20\
        line_format%20%60%7B%7B.message%7D%7D%60%5B24h%5D%5Cn%29%29""";

    //When
    String link = LokiLinkBuilder.builder("http://localhost/loki", "default").query(query).build();

    //Then
    Assertions.assertThat(link).contains(expectedEncodedQuery)
        .contains("schemaVersion=1&panes=%7B%22")
        .contains("%22:%7B%22datasource%22:%22");
  }
}

