package io.github.fmcejudo.redlogs.processor.loki.instant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DataDeserializer extends StdDeserializer<Data> {

    public DataDeserializer() {
        this(Data.class);
    }

    public DataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Data deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        String resultType = jsonNode.get("resultType").asText();
        Iterator<JsonNode> results = jsonNode.get("result").elements();
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
        Iterator<Map.Entry<String, JsonNode>> metric = resultNode.get("metric").fields();
        Map<String, String> metrics = getLabelValuePair(metric);
        Iterator<JsonNode> value = resultNode.get("value").elements();
        long seconds = Double.valueOf(value.next().asText()).longValue();
        String valueString = value.next().asText();
        return new VectorResult(metrics, List.of(new VectorValue(seconds, valueString)));
    }

    private Result streamsResult(final JsonNode resultNode) {
        Iterator<Map.Entry<String, JsonNode>> streams = resultNode.get("stream").fields();
        Map<String, String> streamMap = getLabelValuePair(streams);
        List<StreamsValue> streamsValues = new ArrayList<>();
        Iterator<JsonNode> values = resultNode.get("values").elements();
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
