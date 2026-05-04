package io.github.fmcejudo.redlogs.card.validator;

import io.github.fmcejudo.redlogs.card.CardQueryRequest;

public interface CardQueryValidator {

  CardQueryValidation validate(CardQueryRequest cardQueryRequest);

  public interface CardQueryValidation {

    boolean isSuccess();

    public static CardQueryValidation success() {
      return new SuccessCardQueryValidation();
    }

    public static CardQueryValidation failed() {
      return new FailedCardQueryValidation();
    }

  }

  record SuccessCardQueryValidation() implements CardQueryValidation {

    @Override
    public boolean isSuccess() {
      return true;
    }
  }

  record FailedCardQueryValidation() implements CardQueryValidation {

    @Override
    public boolean isSuccess() {
      return false;
    }
  }
}