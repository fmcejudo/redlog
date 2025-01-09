package io.github.fmcejudo.redlogs.card.writer;

import io.github.fmcejudo.redlogs.card.CardQueryResponse;

public interface CardReportWriter {

  void onNext(CardQueryResponse cardTaskResult);

  void onError(Throwable throwable);

  void onComplete();
}

