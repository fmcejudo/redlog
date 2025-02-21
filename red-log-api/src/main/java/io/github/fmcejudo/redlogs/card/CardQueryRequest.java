package io.github.fmcejudo.redlogs.card;

import java.util.List;

import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;

public interface CardQueryRequest {

    String id();

    String description();

    String executionId();

    String processor();

    List<String> tags();

    CardMetadata metadata();

    CardQueryValidator cardQueryValidator();
}


