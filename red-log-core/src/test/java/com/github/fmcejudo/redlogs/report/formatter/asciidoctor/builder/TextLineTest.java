package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class TextLineTest {

    @Test
    void shouldCreateATextLine() {
        //Given
        AsciiComponent textLine = TextLine.withText("line to show");

        //When
        String content = textLine.content();

        //Then
        Assertions.assertThat(content).isEqualTo("line to show\n");
    }

}