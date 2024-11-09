package io.github.fmcejudo.redlogs.processor.loki.range;

import java.util.List;
import java.util.Map;

record MatrixResult(Map<String, String> metric, List<MatrixValue> value) implements Result {

    @Override
    public MatrixResult matrixResult() {
        return this;
    }
}
