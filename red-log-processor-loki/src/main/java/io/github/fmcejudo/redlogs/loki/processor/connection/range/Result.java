package io.github.fmcejudo.redlogs.loki.processor.connection.range;

interface Result {

    default MatrixResult matrixResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
