package com.github.fmcejudo.redlogs.report.formatter.asciidoctor;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.report.ReportGenerator;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.AsciiComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.ContainerComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.DocumentTitle;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.Item;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.ListItem;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.TextLine;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportItem;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

class AsciiDoctorContentTest {

    ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = ReportGenerator.fromCurrentDate().addSection(sections -> {
            sections.add(new ReportSection(
                    "execution-id", "nullPointerException", "Null Pointer Exceptions", "http://grafana.link",
                    List.of(new ReportItem(Map.of("name", "name", "description", "description"), 1L))
            ));
        });
    }

    @Test
    void shouldCreateADocumentFromReportList() {
        //Given
        Report report = reportGenerator.withApplicationName("report-one").withParams(Map.of("label", "l1")).generate();
        AsciiDoctorContent asciiDoctorContent = new CustomAsciiDoctorContent();
        String date = LocalDate.now().format(ISO_LOCAL_DATE);

        //When
        String content = asciiDoctorContent.content(report);

        //Then
        Assertions.assertThat(content).isEqualTo("""
                                
                = REPORT-ONE
                Report Date: %s
                With Parameters:
                [ label = l1 ]
                               
                == Null Pointer Exceptions
                link:http://grafana.link[grafana link]
                              
                * *description*: description +
                  *name*: name +
                  *count*: 1
                """.formatted(date));
    }

    @Test
    @DisplayName("same report with different params should render under the same application name")
    void shouldRenderSameReportWithDifferentParams() {
        //Given
        Report report = reportGenerator.withApplicationName("report-one")
                .withParams(Map.of("label", "l2"))
                .addSection(l ->
                        l.add(new ReportSection("executionid", "other-section", "Other Section", "http://link",
                                List.of(
                                        new ReportItem(Map.of("name", "one", "description", "description1"), 1L),
                                        new ReportItem(Map.of("name", "two", "description", "description2"), 1L)
                                )))
                ).generate();

        AsciiDoctorContent asciiDoctorContent = new CustomAsciiDoctorContent();
        String date = LocalDate.now().format(ISO_LOCAL_DATE);

        //When
        String content = asciiDoctorContent.content(report);

        //Then
        Assertions.assertThat(content).isEqualTo("""
                                  
                = REPORT-ONE
                Report Date: %s
                With Parameters:
                [ label = l2 ]
                                
                == Null Pointer Exceptions
                link:http://grafana.link[grafana link]
                                 
                * *description*: description +
                  *name*: name +
                  *count*: 1
                                 
                == Other Section
                link:http://link[grafana link]
                                 
                * *description*: description1 +
                  *name*: one +
                  *count*: 1
                * *description*: description2 +
                  *name*: two +
                  *count*: 1
                """.formatted(date));
    }

}

class CustomAsciiDoctorContent implements AsciiDoctorContent {

    @Override
    public String content(final Report report) {
        Assert.notNull(report, "report can not be null");
        return ContainerComponent.create()
                .addComponent(DocumentTitle.level(1).withText(report.applicationName().toUpperCase()))
                .addComponent(TextLine.withText("Report Date: %s".formatted(report.reportDate())))
                .addComponent(TextLine.withText("With Parameters:"))
                .addComponent(TextLine.withText(formatParams(report)))
                .addComponent(generateSection(report))
                .content();
    }


    private AsciiComponent generateSection(final Report report) {

        var sectionContainer = ContainerComponent.create();

        for (ReportSection reportSection : report.sections()) {
            sectionContainer = sectionContainer.addComponent(DocumentTitle.level(2)
                    .withText(reportSection.description())
                    .setLink(reportSection.link(), "grafana link")
            ).addComponent(generateItems(reportSection.items()));
        }
        return sectionContainer;
    }

    private String formatParams(Report report) {
        if (report.params().isEmpty()) {
            return "DEFAULT";
        }
        StringBuilder builder = new StringBuilder("[");
        Iterator<Map.Entry<String, String>> iterator = report.params().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            builder.append(" ").append(param.getKey()).append(" = ").append(param.getValue()).append(" ");
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        return builder.append("]").toString();
    }

    private AsciiComponent generateItems(final List<ReportItem> items) {
        ContainerComponent itemsContainer = ContainerComponent.create();
        for (ReportItem item : items) {
            Map<String, String> map = new HashMap<>(item.labels());
            itemsContainer = itemsContainer.addComponent(
                    ListItem.createList(List.of(Item.fromMap(map, "*count*: " + item.count())))
            );
        }
        return itemsContainer;
    }
}