package com.github.fmcejudo.redlogs.card.converter;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.model.CardType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

class CardValidatorTest {

    CardValidator cardValidator;

    CardFileGenerator cardFileGenerator;

    @BeforeEach
    void setUp() {
        cardValidator = CardValidator.validate(new RangeValidator())
                .thenValidate(new TimeValidator())
                .thenValidate(new ParameterValidator());

        cardFileGenerator = CardFileGenerator.createInstance()
                .withRange("24h")
                .withTime(LocalTime.of(7, 0, 0))
                .withCommonQuery("""
                        {"app"="<applicationName>", "name"="<name>"}
                        """)
                .withParameters(List.of("applicationName", "name"))
                .addQuery(new CardQuery("npe","Null Pointer Exception", CardType.SUMMARY,"{}"));
    }

    @Test
    void shouldValidateCardFile() {
        //Given
        CardFile cardFile = cardFileGenerator.generate();
        CardContext cardContext = CardContext.from("VALID_CARD", Map.of("applicationName", "RANGE", "name", "name"));

        //When
        CardValidation cardValidation = cardValidator.validateOn(cardFile, cardContext);

        //Then
        Assertions.assertThat(cardValidation.isSuccess()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"24", "2min", "m", "xh", "8hours", "something"})
    @NullSource
    void shouldFailOnInvalidRanges(String range) {
        //Given
        CardFile cardFile = cardFileGenerator.withRange(range).generate();
        CardContext cardContext = CardContext.from("VALID_RANGE", Map.of("applicationName", "RANGE", "name", "name"));

        //When
        CardValidation cardValidation = cardValidator.validateOn(cardFile, cardContext);

        //Then
        Assertions.assertThat(cardValidation.isFailure()).isTrue();
        Assertions.assertThat(cardValidation.errors()).containsAnyOf(
                "valid units for range are 'm' (minutes) or 'h' (hours)",
                "it could not parse amount of time",
                "range property on card must not be null"
        );
    }

    @Test
    void shouldFailOnInvalidParameters() {
        //Given
        CardFile cardFile = cardFileGenerator.withParameters(List.of("applicationName", "name", "range")).generate();
        CardContext cardContext = CardContext.from(
                "INVALID_PARAM",
                Map.of("applicationName", "RANGE", "name", "name", "other", "other")
        );

        //When
        CardValidation cardValidation = cardValidator.validateOn(cardFile, cardContext);

        //Then
        Assertions.assertThat(cardValidation.isFailure()).isTrue();
        Assertions.assertThat(cardValidation.errors()).contains(
                "parameters '[range]' not found in parameter map",
                "context parameter defines extra params: other"
        );
    }

    @Test
    void shouldFailOnInvalidTimes() {
        //Given
        CardFile cardFile = cardFileGenerator.withTime(null).generate();
        CardContext cardContext = CardContext.from("TIME", Map.of());

        //When
        CardValidation cardValidation = cardValidator.validateOn(cardFile, cardContext);

        //Then
        Assertions.assertThat(cardValidation.isSuccess()).isFalse();
        Assertions.assertThat(cardValidation.errors())
                .contains("time key is required in card, to execute query with startTime or time as provided");

    }

}