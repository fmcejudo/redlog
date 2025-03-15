package io.github.fmcejudo.redlogs.formatter.asciidoctor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.exception.SectionRenderException;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiConfigBuilder;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiDetailsRender;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiSectionRender;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportItem;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RedlogAsciiDocumentTest {

  @Test
  void shouldCreateAScaffoldingDocument() {
    //Given
    Report report = new Report("TEST", LocalDate.now(), Map.of("country", "Spain"), List.of(
        new ReportSection("10", "My report", "my description", "my link", List.of())
    ));

    //When

    String document = RedlogAsciiDocument.config(config ->
            config.withContentTable().withPagination().withMetadata(Map.of()).build()
        )
        .withTitle(ignore -> "Test")
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .withTitle(ReportSection::description)
        )
        .build(report);

    //Then
    Assertions.assertThat(document).isNotNull();
  }

  @Test
  @DisplayName("it should fail when reporting is rendering a section which does not match with any section render")
  void shouldFailWhenNoSectionRenderMatch() {
    //Given
    Report report = new Report("TEST", LocalDate.now(), Map.of("country", "Spain"), List.of(
        new ReportSection("10", "My report", "my description", "my link", List.of())
    ));

    //When
    SectionRenderException sectionRenderException = Assertions.catchThrowableOfType(() -> RedlogAsciiDocument.config(config ->
            config.withContentTable().withPagination().withMetadata(Map.of()).build()
        )
        .withTitle(ignore -> "Test")
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(r -> false)
            .withTitle(ReportSection::description)
        )
        .build(report), SectionRenderException.class);

    //Then
    Assertions.assertThat(sectionRenderException).hasMessageContaining("There is no matching render for section id My report.10");
  }

  @Test
  @DisplayName("it can render sections with different formats")
  void shouldRenderSectionWithMultipleRenderers() {


    //Given
    Report report = new Report("TEST", LocalDate.now(), Map.of("country", "Spain"), List.of(
        new ReportSection("10", "first-section", "my description", "my link", List.of()),
        new ReportSection("10", "second-section", "my description", "my link", List.of())
    ));

    //When
    String document =  RedlogAsciiDocument.config(config ->
            config.withContentTable().withPagination().withMetadata(Map.of()).build()
        )
        .withTitle(ignore -> "Test")
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(r -> r.id().equals("first-section.10"))
            .withTitle(r -> "FORMAT ONE")
            .withDetailsRender(
                RedlogAsciiDetailsRender.createDetailsRender()
            )
        )
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(r -> r.id().equals("second-section.10"))
            .withTitle(r -> "FORMAT TWO"))
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(r -> false)
            .withTitle(r -> "FORMAT THREE"))
        .build(report);

    //Then
    Assertions.assertThat(document).contains("FORMAT ONE", "FORMAT TWO").doesNotContain("FORMAT THREE");
  }

  @Test
  @DisplayName("it should render a document with sections and details")
  void shouldRenderADocumentWithDetails() {

    //Given
    Report report = new Report("DISNEY+", LocalDate.now(), Map.of("name", "redlog"), List.of(
        new ReportSection("10", "marvel", "Marvel Characters", "http://marvel.com", List.of(
            new ReportItem(Map.of("name", "Captain America"), 1L)
        )),
        new ReportSection("10", "star-wars", "Star Wars Characters", "http://star-wars.com", List.of(
            new ReportItem(Map.of("name", "Anakin Skywalker"), 1L),
            new ReportItem(Map.of("name", "Qui Gon"), 1L),
            new ReportItem(Map.of("name", "Obi-wan Kenobi"), 1L)
        ))
    ));

    //When
    String document = RedlogAsciiDocument.config(RedlogAsciiConfigBuilder::withDefault).withTitle(Report::applicationName)
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(s -> s.reportId().equals("marvel"))
            .withTitle(rs -> String.join(" - ", rs.description(), " [.tag]#marvel#"))
            .withDetailsRender(RedlogAsciiDetailsRender.createDetailsRender())
        )
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(s -> s.reportId().equals("star-wars"))
            .withTitle(rs -> String.join(" - ", rs.description(), " [.tag]#star-wars#"))
            .withDetailsRender(RedlogAsciiDetailsRender.createDetailsRender())
        ).build(report);

    //Then
    Assertions.assertThat(document).isNotNull();
  }


  @Test
  @DisplayName("it should render long values outside of label table")
  void shouldLongValuesRenderOutside() {

    //Given
    Report report = new Report("DISNEY+", LocalDate.now(), Map.of("name", "redlog"), List.of(
        new ReportSection("10", "marvel", "Marvel Characters", "http://marvel.com", List.of(
            new ReportItem(Map.of(
                "name", "Captain America",
                "description", """
                    This is a very long description which should not be render in the main table as it is very long and it might bother
                    the people trying to follow the details of this item in the report.
                    
                    This is a very long description which should not be render in the main table as it is very long and it might bother
                    the people trying to follow the details of this item in the report.
                    
                    This is a very long description which should not be render in the main table as it is very long and it might bother
                    the people trying to follow the details of this item in the report.
                    """
            ), 1L)
        ))
    ));

    //When
    String document = RedlogAsciiDocument.config(RedlogAsciiConfigBuilder::withDefault).withTitle(Report::applicationName)
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(s -> s.reportId().equals("marvel"))
            .withTitle(rs -> String.join(" - ", rs.description(), " [.tag]#marvel#"))
            .withDetailsRender(RedlogAsciiDetailsRender.createDetailsRender())
        )
        .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
            .whenMatching(s -> s.reportId().equals("star-wars"))
            .withTitle(rs -> String.join(" - ", rs.description(), " [.tag]#star-wars#"))
            .withDetailsRender(RedlogAsciiDetailsRender.createDetailsRender())
        ).build(report);

    //Then
    Assertions.assertThat(document).isNotNull();
    System.out.println(document);
  }

}