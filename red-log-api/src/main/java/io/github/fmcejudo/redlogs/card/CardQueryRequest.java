package io.github.fmcejudo.redlogs.card;

import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public interface CardQueryRequest {

    String id();

    String description();

    String executionId();

    String processor();

    CardMetadata metadata();

    CardQueryValidator cardQueryValidator();
}


