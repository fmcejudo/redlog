package com.github.fmcejudo.redlogs.card.engine.writer;

import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponse;

public interface CardResponseWriter {

    CardQueryResponse write(CardQueryResponse cardTaskResult);
}
