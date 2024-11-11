package io.github.fmcejudo.redlogs.card.processor;

import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;

import java.io.Closeable;

@FunctionalInterface
public interface CardProcessor extends Closeable {

    void process(CardRequest cardQuery, CardExecutionWriter executionWriter, CardReportWriter reportWriter);

    default void close() {}
}
