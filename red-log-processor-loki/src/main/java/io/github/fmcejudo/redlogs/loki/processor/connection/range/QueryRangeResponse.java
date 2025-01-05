package io.github.fmcejudo.redlogs.loki.processor.connection.range;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;

@JsonSerialize
public record QueryRangeResponse(String status, Data data) implements LokiResponse {

    @Override
    public boolean isSuccess() {
        return status.equals("success");
    }

    public List<LokiResult> result() {
        if (data.resultType().equalsIgnoreCase("matrix")) {
            return matrixResult();
        }
        return streamsResult().stream().sorted(Comparator.comparing(l -> l.labels().get("time"))).toList();
    }

    private List<LokiResult> streamsResult() {
        return data.result().stream().flatMap(r -> {
            List<StreamsValue> value = r.streamsResult().values();
            Map<String, String> labels = r.streamsResult().stream();
            return value.stream().map(v -> {
                Map<String, String> customLabels = new HashMap<>(labels);
                long minute = NANOSECONDS.toMinutes(Long.parseLong(v.nanoSeconds()));
                Instant instant = Instant.ofEpochMilli(MINUTES.toMillis(minute));
                String time = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Madrid")).format(ISO_LOCAL_DATE_TIME);
                customLabels.put("time", time);
                return new LokiResult(customLabels, 1L);
            });
        }).toList();
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
