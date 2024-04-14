package com.github.fmcejudo.redlogs.client.loki.query;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

@JsonSerialize
record LokiQueryServiceResponse(String status, Data data) implements LokiResponse {

    @Override
    public boolean isSuccess() {
        return status.equals("success");
    }

    public List<LokiResult> result() {
        Map<String, List<LokiResult>> mapResult = data.result().stream().flatMap(r -> {
            //Map<String, String> labels = r.vectorResult().metric();
            List<VectorValue> value = r.vectorResult().value();
            return value.stream().map(v -> {
                Map<String, String> customLabels = new HashMap<>();
                customLabels.put("time", String.valueOf(TimeUnit.SECONDS.toMinutes(v.seconds())));
                return new LokiResult(customLabels, 1L);
            });
        }).collect(Collectors.groupingBy(l -> l.labels().get("time")));

        return mapResult.entrySet().stream()
                .map(e -> {
                    Instant instant = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(Long.parseLong(e.getKey())));
                    String time = LocalDateTime.ofInstant(instant, ZoneOffset.UTC).format(ISO_LOCAL_TIME);
                    return new LokiResult(Map.of("time", time), e.getValue().size());
                })
                .toList();
    }
}
