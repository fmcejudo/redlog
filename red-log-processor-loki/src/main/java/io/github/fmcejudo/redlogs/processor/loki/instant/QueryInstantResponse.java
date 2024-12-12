package io.github.fmcejudo.redlogs.processor.loki.instant;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.fmcejudo.redlogs.processor.loki.LokiResponse;

@JsonSerialize
public record QueryInstantResponse(String status, Data data) implements LokiResponse {

    @Override
    public boolean isSuccess() {
        return status.equals("success");
    }

    public List<LokiResult> result() {
        if (data.resultType().equals("vector")) {
            return vectorResult();
        } else if (data.resultType().equals("stream")) {
            return streamsResult();
        }
        throw new IllegalStateException("result type is not valid");
    }

    private List<LokiResult> vectorResult() {
        return data.result().stream().flatMap(r -> {
            Map<String, String> labels = r.vectorResult().metric();
            List<VectorValue> value = r.vectorResult().value();
            return value.stream().map(v -> new LokiResult(labels, Long.parseLong(v.value())));
        }).toList();
    }

    private List<LokiResult> streamsResult() {
        return data.result().stream().flatMap(r -> {
            Map<String, String> labels = r.streamsResult().stream();
            List<StreamsValue> value = r.streamsResult().values();
            return value.stream().map(v -> new LokiResult(labels, Long.parseLong(v.value())));
        }).toList();
    }
}


