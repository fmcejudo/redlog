package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.execution.domain.Execution;

public interface CardExecutionAppender {

    public void add(Execution execution);
}
