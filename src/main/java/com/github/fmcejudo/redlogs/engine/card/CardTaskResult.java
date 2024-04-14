package com.github.fmcejudo.redlogs.engine.card;

interface CardTaskResult {

    boolean isValid();

    CardReportEntries success();

    CardException failure();
}

record SuccessTaskResult(CardReportEntries success) implements CardTaskResult {

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public CardException failure() {
        throw new IllegalStateException("your result was succeeded");
    }
}

record FailureTaskResult(CardException failure) implements CardTaskResult {

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public CardReportEntries success() {
        throw new IllegalStateException("your request was failure, you need to get the failure method");
    }
}