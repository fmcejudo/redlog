package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;

import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.AsciiComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.ContainerComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.DocumentTitle;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.Item;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.LinkComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.ListItem;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.SectionContainer;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.TextLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
               
                  link:http://link.io[link]
               
                  end of section two
                  """);
    }
}
