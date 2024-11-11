package com.github.fmcejudo.redlogs.report.formatter.asciidoctor;

import io.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.Options;
import org.asciidoctor.Placement;
import org.asciidoctor.SafeMode;


public class AsciiDoctorFormat implements DocumentFormat {

    private final AsciiDoctorContent asciiDoctorContent;

    public AsciiDoctorFormat(final AsciiDoctorContent asciiDoctorContent) {
        this.asciiDoctorContent = asciiDoctorContent;
    }

    @Override
    public String get(final Report report) {
        String content = asciiDoctorContent.content(report);
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
