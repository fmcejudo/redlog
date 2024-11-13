package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultiComponentTest {

    @Test
    void shouldCreateADocument() {
        //Given

        AsciiComponent sectionOne = SectionContainer.startWithComponent(DocumentTitle.level(2).withText("Section one"))
                .add(ListItem.createList(List.of(
                        Item.fromMap(Map.of("name", "value", "description", "content")),
                        new Item("More information to show")
                )))
                .add(TextLine.withText("end of section one"));

        AsciiComponent sectionTwo = SectionContainer.startWithComponent(DocumentTitle.level(2).withText("Section two"))
                .add(() -> "**Custom**\n")
                .add(LinkComponent.link("http://link.io", "link"))
                .add(TextLine.withText("end of section two"));

        AsciiComponent document = ContainerComponent.create()
                .addComponent(sectionOne)
                .addComponent(sectionTwo);

        //When
        String content = document.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
                  
                  == Section one
                
                  * *description*: content +
                    *name*: value
                
                  * More information to show
                
                  end of section one
               
                  == Section two
               
                  **Custom**
               
                  link:++http://link.io++[link]
               
                  end of section two
                  """);
    }
}
