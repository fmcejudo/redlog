package com.github.fmcejudo.redlogs.client.loki;

@FunctionalInterface
public interface LokiClient {

    LokiResponse query(LokiRequest lokiRequest);

    default String getLokiUrl() {
        throw new IllegalStateException("there is no implementation to get loki url");
    }

}

