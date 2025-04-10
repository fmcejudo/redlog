package com.github.fmcejudo.redlogs.card.loader;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.loader.GithubCardLoader.GithubClient;
import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GithubCardLoaderTest {


  GithubCardLoader githubCardLoader;

  @BeforeEach
  void setUp() {

    UnaryOperator<String> cardContentFn = (file) -> """
        parameters: []
        range: <range>
        time: 07:00
        
        queries:
          - id: %s
            description: %s
            processor: LOKI
            type: COUNT
            query: |
               {}
        """.formatted(file, file);

    Map<String, String> contentMap = Stream.of("file", "file_a", "file_b").collect(Collectors.toMap(s -> s, cardContentFn));

    GithubClient githubClient = contentMap::get;
    var redLogGithubProperties = new RedLogGithubProperties();
    redLogGithubProperties.setUrlMapper(contentMap.keySet().stream().collect(Collectors.toMap(s -> s, s -> s)));
    this.githubCardLoader = new GithubCardLoader(redLogGithubProperties, githubClient);
  }

  @Test
  void shouldLoadFileForApplication() {

    //Given
    CardContext cardContext = CardContext.from("file_b", Map.of());

    //When
    CardFile contentFile = githubCardLoader.load(cardContext);

    //Then
    Assertions.assertThat(contentFile.queries()).hasSize(1).allSatisfy(q -> {
      Assertions.assertThat(q.id()).isEqualTo("file_b");
    });
  }

}

