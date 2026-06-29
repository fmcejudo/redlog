package com.github.fmcejudo.redlogs.card.loader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
  public CardQuery deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {

    ObjectCodec codec = parser.getCodec();
    JsonNode node = codec.readTree(parser);

    String id = node.get("id").asText();
    String processor = textOrNull(node, "processor");
    String description = textOrNull(node, "description");
    JsonNode tagsNode = node.get("tags");

    Map<String, String> properties = new HashMap<>();
    node.fields().forEachRemaining(e -> {
      if (List.of("id", "processor", "description", "tags").contains(e.getKey())) {
        return;
      }
      properties.put(e.getKey(), e.getValue().asText());
    });

    if (tagsNode != null) {
      return new CardQuery(id, processor, description, Stream.of(tagsNode.asText().split(",")).map(String::trim).toList(), properties);
    }

    return new CardQuery(id, processor, description, List.of(), properties);
  }

  private String textOrNull(JsonNode node, String fieldName) {
    if (node.has(fieldName)) {
      return node.get(fieldName).asText();
    }
    return null;
  }

}
