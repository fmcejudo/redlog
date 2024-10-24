package com.github.fmcejudo.redlogs.client.loki;

@FunctionalInterface
public interface LokiClient {

    LokiResponse query(LokiRequest lokiRequest);

}

