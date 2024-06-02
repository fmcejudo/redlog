package com.github.fmcejudo.redlogs.card.report.asciidoctor;

import com.github.fmcejudo.redlogs.card.report.Report;
import com.github.fmcejudo.redlogs.card.report.ReportItem;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.AsciiComponent;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.ContainerComponent;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.DocumentTitle;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.Item;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.ListItem;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.SectionContainer;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.builder.TextLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AsciiDoctorContentTest {

    private List<Report> reportList;

    @BeforeEach
    void setUp() {
        Report reportOne = new Report(
                "report-one",
                LocalDate.now(),
                "http://link.io",
                "description one",
                List.of(new ReportItem(Map.of("name", "name1"), 4L)),
                List.of()
        );

        Report reportTwo = new Report(
                "report-two",
                LocalDate.now(),
                "http://link.io",
                "description two",
                List.of(new ReportItem(Map.of("name", "name2"), 10L)),
                List.of()
        );
        reportList = List.of(reportOne, reportTwo);
    }

    @Test
    void shouldCreateADocumentFromReportList() {
        //Given
        AsciiDoctorContent asciiDoctorContent = new CustomAsciiDoctorContent();

        //When
        String content = asciiDoctorContent.content(reportList);

        //Then
        Assertions.assertThat(content).isEqualTo("""
                  
                  == description one
                  
                  Elements: 1
                  
                  * *name*: name1
                    *count*: 4
                  
                  == description two
                 
                  Elements: 1
                  
                  * *name*: name2
                    *count*: 10
                  """);
    }

}

class CustomAsciiDoctorContent implements AsciiDoctorContent {

    @Override
    public String content(final List<Report> reports) {
        ContainerComponent containerComponent = ContainerComponent.create();
        for (Report report : reports) {
            containerComponent = containerComponent.addComponent(generateSection(report));
        }
        return containerComponent.content();
    }

    private AsciiComponent generateSection(final Report report) {
        var sectionContainer = SectionContainer.startWithComponent(DocumentTitle.withText(report.description()));
        sectionContainer = sectionContainer.add(TextLine.withText("Elements: " + report.items().size()));
        for (ReportItem reportItem : report.items()) {
            sectionContainer = sectionContainer.add(generateItems(reportItem));
        }
        return sectionContainer;
    }

    private AsciiComponent generateItems(final ReportItem reportItem) {
        Map<String, String> map = new HashMap<>(reportItem.labels());
        return ListItem.createList(List.of(Item.fromMap(map, "*count*: "+reportItem.count())));
    }
}