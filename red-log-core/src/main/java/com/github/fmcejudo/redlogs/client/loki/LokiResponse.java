package com.github.fmcejudo.redlogs.client.loki;

import java.util.List;
import java.util.Map;

public interface LokiResponse {

    boolean isSuccess();

    List<LokiResult> result();

    public record LokiResult(Map<String, String> labels, long count) {

    }
}


