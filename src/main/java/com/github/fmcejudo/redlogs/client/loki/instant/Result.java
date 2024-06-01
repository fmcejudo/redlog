package com.github.fmcejudo.redlogs.client.loki.instant;

interface Result {

    default VectorResult vectorResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
