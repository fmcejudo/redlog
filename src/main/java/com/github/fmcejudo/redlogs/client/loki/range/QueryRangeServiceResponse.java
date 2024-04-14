package com.github.fmcejudo.redlogs.client.loki.range;

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

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@JsonSerialize
record QueryRangeServiceResponse(String status, Data data) implements LokiResponse {

    @Override
    public boolean isSuccess() {
        return status.equals("success");
    }

    public List<LokiResult> result() {
        Map<String, List<LokiResult>> mapResult;
        if (data.resultType().equalsIgnoreCase("matrix")) {
            mapResult = matrixResult();
        } else {
            mapResult = streamsResult();
        }

        return mapResult.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Instant instant = Instant.ofEpochMilli(TimeUnit.MINUTES.toMillis(Long.parseLong(e.getKey())));
                    String time = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(2)).format(ISO_LOCAL_DATE_TIME);
                    return new LokiResult(Map.of("time", time), e.getValue().size());
                })
                .toList();
    }

    private Map<String, List<LokiResult>> streamsResult() {
        return data.result().stream().flatMap(r -> {
            List<StreamsValue> value = r.streamsResult().values();
            return value.stream().map(v -> {
                Map<String, String> customLabels = new HashMap<>();
                long minute = TimeUnit.NANOSECONDS.toMinutes(Long.parseLong(v.nanoSeconds()));
                customLabels.put("time", String.valueOf(minute));
                return new LokiResult(customLabels, 1L);
            });
        }).collect(Collectors.groupingBy(l -> l.labels().get("time")));
    }


    private Map<String, List<LokiResult>> matrixResult() {
        return data.result().stream().flatMap(r -> {
            List<MatrixValue> value = r.matrixResult().value();
            return value.stream().map(v -> {
                Map<String, String> customLabels = new HashMap<>();
                long minute = TimeUnit.SECONDS.toMinutes(v.seconds());
                customLabels.put("time", String.valueOf(minute));
                return new LokiResult(customLabels, 1L);
            });
        }).collect(Collectors.groupingBy(l -> l.labels().get("time")));
    }
}
