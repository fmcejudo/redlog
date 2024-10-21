package com.github.fmcejudo.redlogs.card.process;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LokiLinkBuilderTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "{filter=\"something\"} with a > b"
    })
    void shouldCreateALokiLinkWithQueries(final String query) {
        //Given

        //When
        String lokiLink = LokiLinkBuilder.builder("http://lokilink.io", "datasource").query(query).build();

        //Then
        Assertions.assertThat(lokiLink).contains("http://lokilink.io");
    }

}