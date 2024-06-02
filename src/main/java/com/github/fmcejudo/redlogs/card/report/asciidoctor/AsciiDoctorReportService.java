package com.github.fmcejudo.redlogs.card.report.asciidoctor;

import com.github.fmcejudo.redlogs.card.report.Report;
import com.github.fmcejudo.redlogs.card.report.ReportService;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.Options;
import org.asciidoctor.Placement;
import org.asciidoctor.SafeMode;

import java.util.List;


public class AsciiDoctorReportService implements ReportService {

    private final AsciiDoctorContent asciiDoctorContent;

    public AsciiDoctorReportService(final AsciiDoctorContent asciiDoctorContent) {
        this.asciiDoctorContent = asciiDoctorContent;
    }

    @Override
    public String get(final String applicationName, final List<Report> reports) {
        String content = asciiDoctorContent.content(reports);
        try (var asciidoctor = Asciidoctor.Factory.create()) {

            AttributesBuilder attributesBuilder = Attributes.builder()
                    .icons(Attributes.FONT_ICONS)
                    .experimental(true)
                    .tableOfContents(true)
                    .tableOfContents(Placement.LEFT)
                    .sectNumLevels(3)
                    .sectionNumbers(true)
                    .hardbreaks(true)
                    .setAnchors(true);

            Attributes attributes = attributesBuilder.build();

            return asciidoctor.convert(content, Options.builder().backend("html5")
                    .standalone(true)
                    .safe(SafeMode.SAFE)
                    .docType("book")
                    .attributes(attributes).build());

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
