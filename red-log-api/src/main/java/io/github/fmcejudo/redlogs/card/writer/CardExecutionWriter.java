package io.github.fmcejudo.redlogs.card.writer;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardMetadata;

public interface CardExecutionWriter {

  public String writeCardExecution(final CardMetadata cardMetadata, final Map<String, String> parameters);

}
