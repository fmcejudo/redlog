package io.github.fmcejudo.redlogs.loki.processor.connection;

@FunctionalInterface
public interface LokiClient {

    LokiResponse query(LokiRequest lokiRequest);

}

