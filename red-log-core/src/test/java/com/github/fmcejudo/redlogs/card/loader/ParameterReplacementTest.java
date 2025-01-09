package com.github.fmcejudo.redlogs.card.loader;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParameterReplacementTest {

  ParameterReplacement parameterReplacement;

  @BeforeEach
  void setUp() {
    this.parameterReplacement = new ParameterReplacement();
  }

  @Test
  void shouldReplaceVariables() {

    //Given
    final String content = """
        The <animal> is <sound>\
        """;
    final Map<String, String> parameters = Map.of("animal", "dog", "sound", "barking");

    //When
    String newContent = parameterReplacement.replace(content, parameters);

    //Then
    Assertions.assertThat(newContent).isEqualTo("The dog is barking");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "The <animal is <sound",
      "The animal> is <sound",
      "The <animal is sound>",
      "The <animal > is < sound>"
  })
  void shouldNotReplaceAnyVariable(String content) {
    //Given
    final Map<String, String> parameters = Map.of("animal", "dog", "sound", "barking");

    //When
    String newContent = parameterReplacement.replace(content, parameters);

    //Then
    Assertions.assertThat(newContent).isEqualTo(content);
  }

  @Test
  void shouldAcceptGreaterThanInContent() {
    //Given
    final String content = """
        <bigAnimal> > <smallAnimal>\
        """;
    Map<String, String> parameters = Map.of("bigAnimal", "elephant", "smallAnimal", "mouse");

    //When
    String newContent = parameterReplacement.replace(content, parameters);

    //Then
    Assertions.assertThat(newContent).isEqualTo("elephant > mouse");
  }

  @Test
  void shouldAcceptLessThanInContent() {
    //Given
    final String content = """
        <smallAnimal> < <bigAnimal>\
        """;
    Map<String, String> parameters = Map.of("bigAnimal", "elephant", "smallAnimal", "mouse");

    //When
    String newContent = parameterReplacement.replace(content, parameters);

    //Then
    Assertions.assertThat(newContent).isEqualTo("mouse < elephant");
  }

  @Test
  void shouldThrowExceptionOnUnknownVariable() {
    //Given
    final String content = """
        You don't know this <animal>
        """;
    final Map<String, String> parameters = Map.of();

    //When

    String newContent = parameterReplacement.replace(content, parameters);

    //Then
    Assertions.assertThat(newContent).isEqualTo(content);
  }

}