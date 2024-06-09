package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;

public interface CardResponseWriter {

    CardQueryResponse write(CardQueryResponse cardTaskResult);
}
