package io.github.fmcejudo.redlogs.card.processor;

import java.io.Closeable;

import io.github.fmcejudo.redlogs.card.processor.filter.ResponseEntryFilter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;

@FunctionalInterface
public interface CardProcessor extends Closeable {

    void process(ProcessorContext processorContext, ResponseEntryFilter filters, CardReportWriter reportWriter);

    default void close() {}
}
