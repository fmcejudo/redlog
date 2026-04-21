package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

class DataDeserializer extends StdDeserializer<Data> {

  public DataDeserializer() {
    this(Data.class);
  }

  public DataDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Data deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

    JsonNode jsonNode = jsonParser.readValueAsTree();
    String resultType = jsonNode.get("resultType").asText();
    Iterator<JsonNode> results = jsonNode.get("result").iterator();
    if (resultType.equalsIgnoreCase("vector")) {
      List<Result> resultList = new ArrayList<>();
      results.forEachRemaining(r -> resultList.add(this.vectorResult(r)));
      return new Data(resultType, resultList);
    } else {
      List<Result> resultList = new ArrayList<>();
      results.forEachRemaining(r -> resultList.add(this.streamsResult(r)));
      return new Data(resultType, resultList);
    }
  }

  private Result vectorResult(final JsonNode resultNode) {
    Iterator<Map.Entry<String, JsonNode>> metric = resultNode.get("metric").properties().iterator();
    Map<String, String> metrics = getLabelValuePair(metric);
    Iterator<JsonNode> value = resultNode.get("value").iterator();
    long seconds = Double.valueOf(value.next().asText()).longValue();
    String valueString = value.next().asText();
    return new VectorResult(metrics, List.of(new VectorValue(seconds, valueString)));
  }

  private Result streamsResult(final JsonNode resultNode) {
    Iterator<Map.Entry<String, JsonNode>> streams = resultNode.get("stream").properties().iterator();
    Map<String, String> streamMap = getLabelValuePair(streams);
    List<StreamsValue> streamsValues = new ArrayList<>();
    Iterator<JsonNode> values = resultNode.get("values").iterator();
    String nanoseconds = values.next().asText();
    String value = values.next().textValue();
    streamsValues.add(new StreamsValue(nanoseconds, value));
    return new StreamsResult(streamMap, streamsValues);
  }

  private Map<String, String> getLabelValuePair(final Iterator<Map.Entry<String, JsonNode>> nodes) {
    HashMap<String, String> map = new HashMap<>();
    nodes.forEachRemaining(e -> map.put(e.getKey(), e.getValue().asText()));
    return map;
  }
}
