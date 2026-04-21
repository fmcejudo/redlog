package com.github.fmcejudo.redlogs.card.loader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import io.github.fmcejudo.redlogs.card.CardQuery;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

public class CardQueryDeserializer extends StdDeserializer<CardQuery> {

  protected CardQueryDeserializer() {
    super(CardQuery.class);
  }

  @Override
  public CardQuery deserialize(JsonParser parser, DeserializationContext deserializationContext) {

    JsonNode node = parser.readValueAsTree();

    String id = readStringNode(node, "id");
    String processor = readStringNode(node, "processor");
    String description = readStringNode(node, "description");
    List<String> tags = readArrayOfStrings(node, "tags");

    Map<String, String> properties = new HashMap<>();
    node.properties().iterator().forEachRemaining(e -> {
      if (List.of("id", "processor", "description", "tags").contains(e.getKey())) {
        return;
      }
      JsonNode jsonNode = e.getValue();
      if (jsonNode instanceof StringNode sn) {
        properties.put(e.getKey(), sn.stringValue());
      } else if (jsonNode instanceof ObjectNode on) {
        Map<String, String> complexMap = resolve(e.getKey(), on.properties().iterator());
        properties.putAll(complexMap);
      }
    });

    return new CardQuery(id, processor, description, tags, properties);
  }

  private String readStringNode(JsonNode node, String property) {
    JsonNode childNode = node.get(property);
    if (childNode == null || childNode.isNull()) {
      return null;
    }
    if (childNode instanceof StringNode stringNode) {
      return stringNode.asString();
    }
    return null;
  }

  private List<String> readArrayOfStrings(JsonNode node, String property) {
    JsonNode childNode = node.get(property);
    if (childNode == null || childNode.isNull()) {
      return List.of();
    }
    if (childNode instanceof StringNode sn) {
      return Arrays.stream(sn.asString().split(",")).map(String::trim).toList();
    }
    if (childNode instanceof ArrayNode arrayNode) {
      return arrayNode.elements().stream().map(JsonNode::asString).toList();
    }
    return List.of();
  }

  private static Map<String, String> resolve(String rootKey, Iterator<Entry<String, JsonNode>> node) {
    Map<String, String> result = new HashMap<>();
    while (node.hasNext()) {
      Entry<String, JsonNode> entry = node.next();
      JsonNode jsonNode = entry.getValue();
      if (jsonNode instanceof StringNode stringNode) {
        result.put(String.join(".", rootKey, entry.getKey()), stringNode.stringValue());
      } else {
        throw new RuntimeException("Multilevel properties is not yet recognised.");
      }
    }
    return Map.copyOf(result);
  }

}
