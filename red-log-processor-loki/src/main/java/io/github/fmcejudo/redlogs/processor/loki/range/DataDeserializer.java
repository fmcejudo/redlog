package io.github.fmcejudo.redlogs.processor.loki.range;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

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
        if (resultType.equalsIgnoreCase("matrix")) {
            List<Result> resultList = new ArrayList<>();
            results.forEachRemaining(r -> resultList.add(this.matrixResult(r)));
            return new Data(resultType, resultList);
        } else {
            List<Result> resultList = new ArrayList<>();
            results.forEachRemaining(r -> resultList.add(this.streamsResult(r)));
            return new Data(resultType, resultList);
        }
    }

    private Result matrixResult(final JsonNode resultNode) {
        Iterator<Map.Entry<String, JsonNode>> metric = resultNode.get("metric").fields();
        Map<String, String> metrics = getLabelValuePair(metric);
        Iterator<JsonNode> value = resultNode.get("values").elements();

        List<MatrixValue> matrixValueList = new ArrayList<>();
        value.forEachRemaining(jsonNode -> {
            Iterator<JsonNode> values = jsonNode.elements();
            long seconds = Double.valueOf(values.next().asText()).longValue();
            String valueString = values.next().asText();
            matrixValueList.add(new MatrixValue(seconds, valueString));
        });
        return new MatrixResult(metrics, matrixValueList);
    }

    private Result streamsResult(final JsonNode resultNode) {
        Iterator<Map.Entry<String, JsonNode>> streams = resultNode.get("stream").fields();
        Map<String, String> streamMap = getLabelValuePair(streams);
        List<StreamsValue> streamsValues = new ArrayList<>();
        resultNode.get("values").elements().forEachRemaining(jsonNode -> {
            Iterator<JsonNode> values = jsonNode.elements();
            String nanoSeconds = values.next().asText();
            String valueString = values.next().asText();
            streamsValues.add(new StreamsValue(nanoSeconds, valueString));
        });
        return new StreamsResult(streamMap, streamsValues);
    }

    private Map<String, String> getLabelValuePair(final Iterator<Map.Entry<String, JsonNode>> nodes) {
        HashMap<String, String> map = new HashMap<>();
        nodes.forEachRemaining(e -> map.put(e.getKey(), e.getValue().asText()));
        return map;
    }
}
