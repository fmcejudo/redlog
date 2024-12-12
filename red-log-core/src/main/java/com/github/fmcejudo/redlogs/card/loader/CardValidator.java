package com.github.fmcejudo.redlogs.card.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.fmcejudo.redlogs.card.CardContext;

@FunctionalInterface
interface CardValidator {

    CardValidation validateOn(CardFile cardFile, CardContext cardContext);

    static CardValidator validate(CardValidator cardValidation) {
        return cardValidation;
    }

    default CardValidator thenValidate(CardValidator cardValidator) {
        return (cardFile, cardContext) ->
                this.validateOn(cardFile, cardContext).then(cardValidator.validateOn(cardFile, cardContext));
    }

}

interface CardValidation {

    static CardValidation invalid(String message) {
        return new FailedCardValidation(List.of(message));
    }

    static CardValidation valid() {
        return new SuccessCardValidation();
    }

    default CardValidation then(CardValidation o) {
        if (this.isSuccess()) {
            return o;
        }
        if (o.isSuccess()) {
            return this;
        }
        List<String> errors = new ArrayList<>(this.errors());
        errors.addAll(o.errors());
        return new FailedCardValidation(Collections.unmodifiableList(errors));
    }

    boolean isSuccess();

    default List<String> errors() {
        return List.of();
    }

    default boolean isFailure() {
        return !isSuccess();
    }
}

record SuccessCardValidation() implements CardValidation {
    @Override
    public boolean isSuccess() {
        return true;
    }
}

record FailedCardValidation(List<String> errors) implements CardValidation {

    @Override
    public boolean isSuccess() {
        return false;
    }
}
