package com.github.fmcejudo.redlogs.card.process;

import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.card.writer.CardResponseWriter;

import java.io.Closeable;

@FunctionalInterface
public interface CardProcessor extends Closeable {

    void process(CardRequest cardQuery, CardResponseWriter writer);

    default void close() {}
}
