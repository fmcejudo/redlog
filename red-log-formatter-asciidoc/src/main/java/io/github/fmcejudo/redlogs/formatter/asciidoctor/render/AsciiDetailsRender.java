package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import io.github.fmcejudo.redlogs.report.domain.ReportItem;

public interface AsciiDetailsRender {


  public static RedlogAsciiDetailsRender defaultRender() {
    return RedlogAsciiDetailsRender.createDetailsRender();
  }

  String content(ReportItem reportItem);
}
