package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SectionContainerTest {

    @Test
    void shouldCreateASection() {
        //Given
        AsciiComponent sectionComponent = SectionContainer.startWithComponent(() -> "this is my section");

        //When
        String document = sectionComponent.content();

        //Then
        Assertions.assertThat(document).isEqualTo("\nthis is my section\n");
    }

    @Test
    void shouldComposeASection() {
        //Given
        AsciiComponent sectionComponent = SectionContainer.startWithComponent(() -> "line 1").add(() -> "line 2");

        //When
        String document = sectionComponent.content();

        //Then
        Assertions.assertThat(document).isEqualTo("""
                
                line 1
                
                line 2""");

    }
}