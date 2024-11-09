package io.github.fmcejudo.redlogs.processor.loki.instant;

interface Result {

    default VectorResult vectorResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
