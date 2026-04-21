package com.github.fmcejudo.redlogs.card.validator;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.loader.CardFile;

@FunctionalInterface
public interface CardValidator {

    CardValidation validateOn(CardFile cardFile, CardContext cardContext);

    static CardValidator validate(CardValidator cardValidation) {
        return cardValidation;
    }

    default CardValidator thenValidate(CardValidator cardValidator) {
        return (cardFile, cardContext) ->
                this.validateOn(cardFile, cardContext).then(cardValidator.validateOn(cardFile, cardContext));
    }

    static CardValidator defaultValidator() {
        return new DefaultCardValidator();
    }

}
