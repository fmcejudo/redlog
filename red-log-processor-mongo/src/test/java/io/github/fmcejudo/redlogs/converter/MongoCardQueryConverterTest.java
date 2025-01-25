package io.github.fmcejudo.redlogs.converter;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fmcejudo.redlogs.MongoRedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MongoCardQueryConverterTest {

  CardQueryConverter cardQueryConverter;

  CardMetadata cardMetadata;

  @BeforeEach
  void setUp() {
    this.cardQueryConverter = new MongoRedlogPluginProvider().createCardQueryConverter();
    this.cardMetadata = new CardMetadata("40", "test", LocalDateTime.now().minusHours(1), LocalDateTime.now());
  }

  @Test
  void shouldFailWithoutCardType() {
    //Given
    CardQuery cardQuery = new CardQuery("mongo-count-card", "MONGO", "mongo-count-card", Map.of());

    //When
    Throwable throwable = Assertions.catchThrowable(() -> cardQueryConverter.convert(cardQuery, cardMetadata));

    //Then
    Assertions.assertThat(throwable)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Mongo card requires of a type: LIST or COUNT");
  }

  @Test
  void shouldFailOnWrongCardType() {
    //Given
    CardQuery cardQuery = new CardQuery("mongo-count-card", "MONGO", "mongo-count-card", Map.of("type", "BAD"));

    //When
    Throwable throwable = Assertions.catchThrowable(() -> cardQueryConverter.convert(cardQuery, cardMetadata));

    //Then
    Assertions.assertThat(throwable)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("MONGO cards only accepts LIST and COUNT");
  }

  @Test
  void shouldCreateAMongoCountCard() {
    //Given
    CardQuery cardQuery = new CardQuery("mongo-count-card", "MONGO", "mongo-count-card", Map.of("type", "COUNT"));
    CardMetadata cardMetadata = new CardMetadata("40", "test", LocalDateTime.now().minusHours(1), LocalDateTime.now());

    //When
    CardQueryRequest cardQueryRequest = cardQueryConverter.convert(cardQuery, cardMetadata);

    //Then
    Assertions.assertThat(cardQueryRequest).isInstanceOf(MongoCountCardRequest.class)
        .asInstanceOf(InstanceOfAssertFactories.type(MongoCountCardRequest.class))
        .satisfies(mccr -> {
          Assertions.assertThat(mccr.id()).isEqualTo("mongo-count-card");
        });
  }

  @Test
  void shouldCreateAMongoListCard() {
    //Given
    CardQuery cardQuery = new CardQuery("mongo-list-card", "MONGO", "mongo-list-card", Map.of("type", "LIST"));
    CardMetadata cardMetadata = new CardMetadata("50", "test", LocalDateTime.now().minusHours(1), LocalDateTime.now());

    //When
    CardQueryRequest cardQueryRequest = cardQueryConverter.convert(cardQuery, cardMetadata);

    //Then
    Assertions.assertThat(cardQueryRequest).isInstanceOf(MongoListCardRequest.class)
        .asInstanceOf(InstanceOfAssertFactories.type(MongoListCardRequest.class))
        .satisfies(mccr -> {
          Assertions.assertThat(mccr.id()).isEqualTo("mongo-list-card");
        });
  }
}