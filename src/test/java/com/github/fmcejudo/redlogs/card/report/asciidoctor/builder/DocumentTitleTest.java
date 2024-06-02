package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DocumentTitleTest {

    @Test
    void shouldCreateATitleComponent() {
        //Given
        AsciiComponent documentTitle = DocumentTitle.withText("title");

        //When
        String content = documentTitle.content();

        //Then
        Assertions.assertThat(content).isEqualTo("== title");
    }

    @Test
    void shouldCreateATitleWithLink() {
        //Given
        AsciiComponent documentTitle = DocumentTitle.withText("title").setLink("http://bit.io/link", "description");

        //When
        String content = documentTitle.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
                == title +
                link:http://bit.io/link[description]""");

    }

}