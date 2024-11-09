package io.github.fmcejudo.redlogs.processor.loki.range;

interface Result {

    default MatrixResult matrixResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
