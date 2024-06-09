package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;

import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.AsciiComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.LinkComponent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LinkComponentTest {

    @Test
    void shouldCreateALink() {
        //Given
        AsciiComponent link = LinkComponent.link("http://something.io", "description");

        //When
        String content = link.content();

        //Then
        Assertions.assertThat(content).isEqualTo("link:http://something.io[description]\n");
    }

}