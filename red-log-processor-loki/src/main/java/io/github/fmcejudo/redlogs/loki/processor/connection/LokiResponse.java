package io.github.fmcejudo.redlogs.loki.processor.connection;

import java.util.List;
import java.util.Map;

public interface LokiResponse {

    boolean isSuccess();

    List<LokiResult> result();

    public static record LokiResult(Map<String, String> labels, long count) {

    }
}


