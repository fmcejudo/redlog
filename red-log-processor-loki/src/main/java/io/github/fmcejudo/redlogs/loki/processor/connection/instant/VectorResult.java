package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

import java.util.List;
import java.util.Map;

record VectorResult(Map<String, String> metric, List<VectorValue> value) implements Result {

    @Override
    public VectorResult vectorResult() {
        return this;
    }
}
