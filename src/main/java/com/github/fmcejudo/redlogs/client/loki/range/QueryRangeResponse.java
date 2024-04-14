package com.github.fmcejudo.redlogs.client.loki.range;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;

import java.util.List;
import java.util.Map;

@JsonSerialize
record QueryRangeResponse(String status, Data data) implements LokiResponse {

    @Override
    public boolean isSuccess() {
        return status.equals("success");
    }

    public List<LokiResult> result() {
        return data.result().stream().flatMap(r -> {
            Map<String, String> labels = r.matrixResult().metric();
            List<MatrixValue> value = r.matrixResult().value();
            return value.stream().map(v -> new LokiResult(labels, Long.parseLong(v.value())));
        }).toList();
    }
}


