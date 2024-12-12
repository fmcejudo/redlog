package io.github.fmcejudo.redlogs.processor.loki;

@FunctionalInterface
public interface LokiClient {

    LokiResponse query(LokiRequest lokiRequest);

}

