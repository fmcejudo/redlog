package com.github.fmcejudo.redlogs.engine.card.writer;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;

public interface CardResponseWriter {

    CardQueryResponse write(CardQueryResponse cardTaskResult);
}
