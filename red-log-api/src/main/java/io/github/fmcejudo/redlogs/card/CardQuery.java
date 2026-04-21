package io.github.fmcejudo.redlogs.card;

import java.util.List;
import java.util.Map;

public record CardQuery(String id, String processor, String description, List<String> tags, Map<String, String> properties) {

  public CardQuery {
    if (tags == null || tags.isEmpty()) {
      tags = List.of();
    } else {
      tags = List.copyOf(tags);
    }

    if (properties == null || properties.isEmpty()) {
      properties = Map.of();
    } else {
      properties = Map.copyOf(properties);
    }
  }
}
