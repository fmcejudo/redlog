package io.github.fmcejudo.redlogs.card.validator;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;

public interface CardQueryValidator {

  void validate(CardQueryRequest cardQueryRequest);
}
