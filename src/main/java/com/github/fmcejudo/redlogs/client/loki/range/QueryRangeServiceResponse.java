package com.github.fmcejudo.redlogs.client.loki.range;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.fmcejudo.redlogs.client.loki.LokiResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.ZoneOffset.*;
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
            return matrixResult();
        } else {
            mapResult = streamsResult();
        }

        return mapResult.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Instant instant = Instant.ofEpochMilli(TimeUnit.MINUTES.toMillis(Long.parseLong(e.getKey())));
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
                long minute = TimeUnit.NANOSECONDS.toMinutes(Long.parseLong(v.nanoSeconds()));
                customLabels.put("time", String.valueOf(minute));
                return new LokiResult(customLabels, 1L);
            });
        }).collect(Collectors.groupingBy(l -> l.labels().get("time")));
    }

    private List<LokiResult> matrixResult() {
        return data.result().stream().flatMap(this::evaluateMatrixResult).distinct().toList();
    }

    private Stream<LokiResult> evaluateMatrixResult(final Result r) {
        List<MatrixValue> value = r.matrixResult().value()
                .stream().sorted(Comparator.comparing(MatrixValue::seconds)).toList();
        String service = tryFindServiceURL(r);

        List<LokiResult> result = new ArrayList<>();
        long rangeStart = -1;
        long rangeEnd = -1;
        for (MatrixValue matrixValue : value) {
            long minute = TimeUnit.SECONDS.toMinutes(matrixValue.seconds());
            if (rangeStart == -1) {
                rangeStart = minute;
                rangeEnd = minute;
                continue;
            }
            if (minute == rangeEnd + 1) {
                rangeEnd = minute;
            } else {
                result.add(new LokiResult(serviceLabels(rangeStart, rangeEnd, service), 1));
                rangeStart = -1;
                rangeEnd = -1;
            }
        }
        if (rangeEnd != -1) {
            result.add(new LokiResult(serviceLabels(rangeStart, rangeEnd, service), 1));
        }
        return result.stream();
    }

    private Map<String, String> serviceLabels(long rangeStart, long rangeEnd, String service) {
        String start = LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUnit.MINUTES.toMillis(rangeStart)), ZoneId.of("Europe/Madrid"))
                .format(ISO_LOCAL_DATE_TIME);
        String end = LocalDateTime.ofInstant(Instant.ofEpochMilli(TimeUnit.MINUTES.toMillis(rangeEnd)), ZoneId.of("Europe/Madrid"))
                .format(ISO_LOCAL_DATE_TIME);
        return Map.of("start", start, "end", end, "service", service);
    }

    private String tryFindServiceURL(Result r) {
        Map<String, String> metric = r.matrixResult().metric();
        String shortMessage = metric.get("short_message");
        if (shortMessage == null) {
            return "unknown";
        }
        int httpIndex = shortMessage.indexOf("https:");
        int spaceUpToIndex = shortMessage.indexOf(" ", httpIndex);
        int enterUpToIndex = shortMessage.indexOf("\n", httpIndex);
        return shortMessage.substring(httpIndex, Math.min(spaceUpToIndex, enterUpToIndex));
    }
}
