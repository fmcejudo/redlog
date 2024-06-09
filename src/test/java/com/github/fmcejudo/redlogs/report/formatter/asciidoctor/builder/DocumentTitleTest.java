package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DocumentTitleTest {

    @Test
    void shouldCreateATitleComponent() {
        //Given
        AsciiComponent documentTitle = DocumentTitle.level(2).withText("title");

        //When
        String content = documentTitle.content();

        //Then
        Assertions.assertThat(content).isEqualTo("\n== title\n");
    }

    @Test
    void shouldCreateATitleWithLink() {
        //Given
        AsciiComponent documentTitle =
                DocumentTitle.level(2).withText("title").setLink("http://bit.io/link", "description");

        //When
        String content = documentTitle.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
                
                == title +
                link:http://bit.io/link[description]
                
                """);

    }

}