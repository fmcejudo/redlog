package io.github.fmcejudo.redlogs.card.writer;

import java.util.Map;

public interface CardWriterProvider {

  CardResponseWriter cardResponseWriter(Map<String, String> writerConnectionParams);
}
