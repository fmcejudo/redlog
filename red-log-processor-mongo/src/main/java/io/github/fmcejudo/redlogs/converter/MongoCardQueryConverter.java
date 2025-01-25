package io.github.fmcejudo.redlogs.converter;

import io.github.fmcejudo.redlogs.card.MongoCountCardRequest;
import io.github.fmcejudo.redlogs.card.MongoListCardRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import org.springframework.util.Assert;

@FunctionalInterface
public interface MongoCardQueryConverter extends CardQueryConverter  {

  public static CardQueryConverter createInstance() {
    return (cardQuery, cardMetadata) -> {
      String type = cardQuery.properties().get("type");
      Assert.notNull(type, "Mongo card requires of a type: LIST or COUNT");
      return switch (type.toUpperCase()) {
        case "COUNT" -> new MongoCountCardRequest(cardQuery, cardMetadata);
        case "LIST" -> new MongoListCardRequest(cardQuery, cardMetadata);
        default -> throw new IllegalStateException("MONGO cards only accepts LIST and COUNT");
      };
    };
  }
}
