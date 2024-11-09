package io.github.fmcejudo.redlogs.processor.loki;

import java.util.List;
import java.util.Map;

public interface LokiResponse {

    boolean isSuccess();

    List<LokiResult> result();

    public record LokiResult(Map<String, String> labels, long count) {

    }
}


