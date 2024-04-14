package com.github.fmcejudo.redlogs.client.loki.range;

import java.util.List;
import java.util.Map;

record StreamsResult(Map<String, String> stream, List<StreamsValue> values) implements Result {

    @Override
    public StreamsResult streamsResult() {
        return this;
    }
}
