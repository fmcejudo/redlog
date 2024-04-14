package com.github.fmcejudo.redlogs.client.loki.range;

interface Result {

    default MatricResult matrixResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
