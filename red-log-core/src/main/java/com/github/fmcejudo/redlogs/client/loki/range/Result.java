package com.github.fmcejudo.redlogs.client.loki.range;

interface Result {

    default MatrixResult matrixResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
