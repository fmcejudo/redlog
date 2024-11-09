package io.github.fmcejudo.redlogs.card.writer;

import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;

public interface CardResponseWriter {

    void writeExecution(CardRequest cardRequest);

    void onNext(CardQueryResponse cardTaskResult);

    void onError(Throwable throwable);

    void onComplete();
}

