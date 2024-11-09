package io.github.fmcejudo.redlogs.processor.loki;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class LokiLinkBuilderTest {

    @Test
    void shouldCreateALokiLinkWithQueries() {
        //Given
        final String query = """
                {filter="something"} | regexp `\\"(?P<def>.*)\\"`""";

        /* This encoded query requires especial characters encoding as the standard one is not enough,
           it needed to encode: (, ), ?, \, "
         */
        String encodedQuery = """
        expr%22:%22%7Bfilter%3D%5C%22something%5C%22%7D%20%7C%20\
        regexp%20%60%5C%5C%5C%22%28%3FP%3Cdef%3E.%2A%29%5C%5C%5C%22%60%22""";

        //When
        String lokiLink = LokiLinkBuilder.builder("http://loki.io", "datasource")
                .query(query)
                .from(LocalDateTime.now().minusHours(1))
                .to(LocalDateTime.now())
                .build();

        //Then
        Assertions.assertThat(lokiLink).contains("http://loki.io").contains(encodedQuery);

    }

}