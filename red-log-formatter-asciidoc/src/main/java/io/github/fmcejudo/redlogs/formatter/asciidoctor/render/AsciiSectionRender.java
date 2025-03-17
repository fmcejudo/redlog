package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import io.github.fmcejudo.redlogs.report.domain.ReportSection;

public interface AsciiSectionRender {

  public static RedlogAsciiSectionRender defaultRender() {
    return RedlogAsciiSectionRender.createSectionRender();
  }

  String render(ReportSection reportSection);

  boolean match(ReportSection reportSection);
}
