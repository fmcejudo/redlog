package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ListItemTest {

    @Test
    void shouldCreateABulletList() {
        //Given
        Item item = new Item("my bullet content");
        AsciiComponent listItem = ListItem.createList(List.of(item));

        //When
        String content = listItem.content();

        //Then
        Assertions.assertThat(content).isEqualTo("* my bullet content\n");
    }

    @Test
    void shouldCreateABulletListWithMultipleEntries() {
        //Given
        Item item1 = new Item("I am item 1");
        Item item2 = new Item("I am item 2");
        Item item3 = new Item("I am item 3");
        AsciiComponent listItem = ListItem.createList(List.of(item1, item2, item3));

        //When
        String content = listItem.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
                * I am item 1
                
                * I am item 2
                
                * I am item 3
                """);
    }

    @Test
    void shouldCreateBulletMultiline() {
        //Given
        Item item1 = new Item("""
                This is a multiline
                content
                """);
        Item item2 = new Item("""
                This is another
                multiline content
                """);
        AsciiComponent listItem = ListItem.createList(List.of(item1, item2));

        //When
        String content = listItem.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
                * This is a multiline
                  content
                
                * This is another
                  multiline content
                """);
    }

    @Test
    void shouldComposeItems() {
        //Given
        Item item1 = new Item("I am item 1");
        Item item2 = new Item("I am item 2");
        AsciiComponent listItem = ListItem.createList(List.of(item1)).addItem(item2);

        //When
        String content = listItem.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
                * I am item 1
                
                * I am item 2
                """);
    }

    @Test
    void shouldCreateBulletFromMapItems() {
        //Given
        Map<String, String> anakin = Map.of("name", "Anakin Skywalker", "group", "Rebel Alliance", "strength", "3000");
        Map<String, String> sidious = Map.of("name", "Lord Sidious", "group", "Galactic Empire", "strength", "2500");
        Item item1 = Item.fromMap(anakin);
        Item item2 = Item.fromMap(sidious);
        AsciiComponent listItem = ListItem.createList(List.of(item1)).addItem(item2);

        //When
        String content = listItem.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
              * *group*: Rebel Alliance +
                *name*: Anakin Skywalker +
                *strength*: 3000
             
              * *group*: Galactic Empire +
                *name*: Lord Sidious +
                *strength*: 2500
              """);
    }

    @Test
    void shouldCreateBulletWithEnrichMap() {
        //Given
        Map<String, AsciiComponent> anakin = Map.of(
                "name", LinkComponent.link("http://star.wars/anakin","Anakin Skywalker"),
                "group", TextLine.withText("Rebel Alliance")
        );

        Map<String, AsciiComponent> sidious = Map.of(
                "name", LinkComponent.link("http://star.wars/lordSidious","Lord Sidious"),
                "group", TextLine.withText("Galactic Empire")
        );
        Item item1 = Item.fromEnrichMap(anakin);
        Item item2 = Item.fromEnrichMap(sidious);
        AsciiComponent listItem = ListItem.createList(List.of(item1)).addItem(item2);

        //When
        String content = listItem.content();

        //Then
        Assertions.assertThat(content).isEqualTo("""
          * *group*: Rebel Alliance +
            *name*: link:http://star.wars/anakin[Anakin Skywalker]
         
          * *group*: Galactic Empire +
            *name*: link:http://star.wars/lordSidious[Lord Sidious]
          """);
    }
}