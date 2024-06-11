package com.github.fmcejudo.redlogs.report.formatter.asciidoctor;

import com.github.fmcejudo.redlogs.report.ReportGenerator;
import com.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.domain.ReportItem;
import com.github.fmcejudo.redlogs.report.domain.ReportSection;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.AsciiComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.ContainerComponent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.DocumentTitle;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.Item;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.ListItem;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder.SectionContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

class AsciiDoctorContentTest {

    ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = ReportGenerator.fromCurrentDate().addSection(sections -> {
            sections.add(new ReportSection(
                    "nullPointerException", "Null Pointer Exceptions", "http://grafana.link",
                    List.of(new ReportItem(Map.of("name", "name", "description", "description"), 1L))
            ));
        });
    }

    @Test
    void shouldCreateADocumentFromReportList() {
        //Given
        List<Report> reportList = List.of(
                reportGenerator.withApplicationName("report-one").withParams(Map.of("label", "l1")).generate(),
                reportGenerator.withApplicationName("report-two").withParams(Map.of("label", "l2")).generate()
        );

        AsciiDoctorContent asciiDoctorContent = new CustomAsciiDoctorContent();
        String date = LocalDate.now().format(ISO_LOCAL_DATE);

        //When
        String content = asciiDoctorContent.content(reportList);

        //Then
        Assertions.assertThat(content).isEqualTo("""
                                 
                = Report Date: %s
                            
                == REPORT-ONE
                                 
                === [ label = l1 ]
                                 
                                 
                ==== Null Pointer Exceptions
                link:http://grafana.link[grafana link]
                                 
                                 
                * *description*: description +
                  *name*: name +
                  *count*: 1
                                 
                == REPORT-TWO
                                 
                === [ label = l2 ]
                                 
                                 
                ==== Null Pointer Exceptions
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
        List<Report> reportList = List.of(
                reportGenerator.withApplicationName("report-one").withParams(Map.of("label", "l1")).generate(),

                reportGenerator.withApplicationName("report-one")
                        .withParams(Map.of("label", "l2"))
                        .addSection(l ->
                                l.add(new ReportSection("other-section", "Other Section", "http://link", List.of(
                                        new ReportItem(Map.of("name", "one", "description", "description1"), 1L),
                                        new ReportItem(Map.of("name", "two", "description", "description2"), 1L)
                                )))
                        ).generate()
        );

        AsciiDoctorContent asciiDoctorContent = new CustomAsciiDoctorContent();
        String date = LocalDate.now().format(ISO_LOCAL_DATE);

        //When
        String content = asciiDoctorContent.content(reportList);

        //Then
        Assertions.assertThat(content).isEqualTo("""
                                 
                = Report Date: %s
                            
                == REPORT-ONE
                                 
                === [ label = l1 ]
                                 
                                 
                ==== Null Pointer Exceptions
                link:http://grafana.link[grafana link]
                                 
                                 
                * *description*: description +
                  *name*: name +
                  *count*: 1
                                 
                === [ label = l2 ]
                                 
                                 
                ==== Null Pointer Exceptions
                link:http://grafana.link[grafana link]
                                 
                                 
                * *description*: description +
                  *name*: name +
                  *count*: 1
                
                
                ==== Other Section
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
    public String content(final List<Report> reports) {
        ContainerComponent containerComponent = ContainerComponent.create().addComponent(reportDateComponent(reports));

        List<Report> sortedReports = reports.stream().sorted(Comparator.comparing(Report::applicationName)).toList();

        String lastName = null;
        for (Report report : sortedReports) {
            if (!report.applicationName().equals(lastName)) {
                lastName = report.applicationName();
                containerComponent = containerComponent
                        .addComponent(DocumentTitle.level(2).withText(report.applicationName().toUpperCase()));
            }
            containerComponent = containerComponent.addComponent(generateSection(report));
        }
        return containerComponent.content();
    }

    private AsciiComponent reportDateComponent(List<Report> reports) {
        if (reports.isEmpty()) {
            return () -> "There is no reports to show";
        }
        String date = reports.getFirst().reportDate().format(ISO_LOCAL_DATE);
        return DocumentTitle.level(1).withText("Report Date: %s".formatted(date));
    }

    private AsciiComponent generateSection(final Report report) {

        var sectionContainer = SectionContainer.startWithComponent(
                DocumentTitle.level(3).withText(formatParams(report))
        );

        for (ReportSection reportSection : report.sections()) {
            sectionContainer = sectionContainer.add(DocumentTitle.level(4)
                    .withText(reportSection.description())
                    .setLink(reportSection.link(), "grafana link")
            ).add(generateItems(reportSection.items()));
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