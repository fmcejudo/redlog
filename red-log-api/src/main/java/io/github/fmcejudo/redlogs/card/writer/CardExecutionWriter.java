package io.github.fmcejudo.redlogs.card.writer;

import io.github.fmcejudo.redlogs.card.domain.CardRequest;

public interface CardExecutionWriter {

  public String writeCardExecution(final CardRequest cardRequest);

}
