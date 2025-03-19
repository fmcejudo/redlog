package io.github.fmcejudo.redlogs.formatter;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.RedlogAsciiDocument;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiConfigBuilder;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiDetailsRender;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiSectionRender;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import io.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;

public class AsciiDoctorDocumentFormat implements DocumentFormat {

  @Override
  public String get(final Report report) {

    try (var asciidoctor = Asciidoctor.Factory.create()) {

      String document = RedlogAsciiDocument.config(RedlogAsciiConfigBuilder::withDefault).withTitle(Report::applicationName)
          .withSectionRender(RedlogAsciiSectionRender.createSectionRender()
              .whenMatching(s -> true)
              .withTitle(ReportSection::description)
              .withDetailsRender(RedlogAsciiDetailsRender.createDetailsRender())
          ).build(report);

      return asciidoctor.convert(document, Options.builder().backend("html5")
          .standalone(true)
          .safe(SafeMode.SAFE)
          .docType("book").build());
    }
  }

  @Override
  public String format() {
    return "pdf";
  }
}
