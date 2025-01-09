package com.github.fmcejudo.redlogs.card.loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.fmcejudo.redlogs.card.CardQuery;

public class CardQueryDeserializer extends StdDeserializer<CardQuery> {

  protected CardQueryDeserializer() {
    super(CardQuery.class);
  }

  @Override
  public CardQuery deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException,
      JacksonException {

    ObjectCodec codec = parser.getCodec();
    JsonNode node = codec.readTree(parser);

    String id = node.get("id").asText();
    String processor = node.get("processor").asText();
    String description = node.get("description").asText();

    Map<String, String> properties = new HashMap<>();
    node.fields().forEachRemaining(e -> {
      if (List.of("id", "processor", "description").contains(e.getKey())) {
        return;
      }
      properties.put(e.getKey(), e.getValue().asText());
    });

    return new CardQuery(id, processor, description, properties);
  }

}
