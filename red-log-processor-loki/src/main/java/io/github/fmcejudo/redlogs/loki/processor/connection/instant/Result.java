package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

interface Result {

    default VectorResult vectorResult() {
        throw new IllegalStateException();
    }

    default StreamsResult streamsResult() {
        throw new IllegalStateException();
    }
}
