package com.github.fmcejudo.redlogs.client.loki.range;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.ZoneOffset.ofHours;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@JsonSerialize
public record QueryRangeResponse(String status, Data data) implements LokiResponse {

    @Override
    public boolean isSuccess() {
        return status.equals("success");
    }

    public List<LokiResult> result() {
        Map<String, List<LokiResult>> mapResult;
        if (data.resultType().equalsIgnoreCase("matrix")) {
            return matrixResult();
        } else {
            mapResult = streamsResult();
        }

        return mapResult.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Instant instant = Instant.ofEpochMilli(MINUTES.toMillis(Long.parseLong(e.getKey())));
                    String time = LocalDateTime.ofInstant(instant, ofHours(2)).format(ISO_LOCAL_DATE_TIME);
                    return new LokiResult(Map.of("time", time), e.getValue().size());
                })
                .toList();
    }

    private Map<String, List<LokiResult>> streamsResult() {
        return data.result().stream().flatMap(r -> {
            List<StreamsValue> value = r.streamsResult().values();
            return value.stream().map(v -> {
                Map<String, String> customLabels = new HashMap<>();
                long minute = NANOSECONDS.toMinutes(Long.parseLong(v.nanoSeconds()));
                customLabels.put("time", String.valueOf(minute));
                return new LokiResult(customLabels, 1L);
            });
        }).collect(Collectors.groupingBy(l -> l.labels().get("time")));
    }

    private List<LokiResult> matrixResult() {
        return data.result().stream().flatMap(this::evaluateMatrixResult).distinct().toList();
    }

    private Stream<LokiResult> evaluateMatrixResult(final Result r) {
        Map<String, String> metric = r.matrixResult().metric();
        List<MatrixValue> value = r.matrixResult().value()
                .stream().sorted(Comparator.comparing(MatrixValue::seconds)).toList();

        List<LokiResult> result = new ArrayList<>();
        long rangeStart = -1;
        long rangeEnd = -1;
        for (MatrixValue matrixValue : value) {
            long minute = SECONDS.toMinutes(matrixValue.seconds());
            if (rangeStart == -1) {
                rangeStart = minute;
                rangeEnd = minute;
                continue;
            }
            if (minute == rangeEnd + 1) {
                rangeEnd = minute;
            } else {
                result.add(new LokiResult(serviceLabels(rangeStart, rangeEnd, metric), 1));
                rangeStart = -1;
                rangeEnd = -1;
            }
        }
        if (rangeEnd != -1) {
            result.add(new LokiResult(serviceLabels(rangeStart, rangeEnd, metric), 1));
        }
        return result.stream();
    }

    private Map<String, String> serviceLabels(long rangeStart, long rangeEnd, final Map<String, String> metricMap) {
        String start = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(MINUTES.toMillis(rangeStart)), ZoneId.of("Europe/Madrid")
        ).format(ISO_LOCAL_DATE_TIME);

        String end = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(MINUTES.toMillis(rangeEnd)), ZoneId.of("Europe/Madrid")
        ).format(ISO_LOCAL_DATE_TIME);

        Map<String, String> map = new HashMap<>(metricMap);
        map.put("start", start);
        map.put("end", end);

        return Collections.unmodifiableMap(map);
    }

}
