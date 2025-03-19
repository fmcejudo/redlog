package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import io.github.fmcejudo.redlogs.report.domain.Report;

@FunctionalInterface
public interface AsciiDocumentRender {

  public static RedlogAsciiDocumentRender defaultRender(RedlogAsciiConfig config) {
    return RedlogAsciiDocumentRender.withConfig(config);
  }

  public String render(Report report);

}
