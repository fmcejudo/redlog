package com.github.fmcejudo.redlogs.card.process;

import java.time.LocalDate;
import java.util.List;

import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardProcessorTest {

  CardProcessor cardProcessor;

  @BeforeEach
  void setUp() {
    cardProcessor = new DefaultCardProcessor();
  }

  @Test
  void shouldRegisterAProcessor() {
    //Given When && Then
    Assertions.assertThat(cardProcessor.register("test", TestCardQueryResponse::from)).isTrue();
    Assertions.assertThat(cardProcessor.register("test", TestCardQueryResponse::from)).isFalse();
  }

  @Test
  void shouldDeregisterAProcessor() {
    //Given When && Then
    Assertions.assertThat(cardProcessor.register("test", TestCardQueryResponse::from)).isTrue();
    Assertions.assertThat(cardProcessor.deregister("test")).isTrue();
    Assertions.assertThat(cardProcessor.deregister("test")).isFalse();
  }

  @Test
  void shouldProcessACardRequest() {
    //Given
    cardProcessor.register("test", TestCardQueryResponse::from);

    CardQueryRequest cardQueryRequest = new TestCardQueryRequest("id");

    //When
    CardQueryResponse response = cardProcessor.process(cardQueryRequest);

    //Then
    Assertions.assertThat(cardProcessor.deregister("test")).isTrue();
    Assertions.assertThat(response.id()).isEqualTo("id");
  }
}

class TestCardQueryResponse {

  static CardQueryResponse from(CardQueryRequest cardQueryRequest) {
   return CardQueryResponse.success(
       LocalDate.now(), cardQueryRequest.id(), cardQueryRequest.executionId(),
       cardQueryRequest.description(), "link", cardQueryRequest.tags(), List.of()
   );
  }
}

class TestCardQueryRequest implements CardQueryRequest {

  private final String id;

  TestCardQueryRequest(String id) {
    this.id = id;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String description() {
    return "";
  }

  @Override
  public String executionId() {
    return "";
  }

  @Override
  public String processor() {
    return "test";
  }

  @Override
  public List<String> tags() {
    return List.of();
  }

  @Override
  public CardMetadata metadata() {
    return null;
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return null;
  }
}