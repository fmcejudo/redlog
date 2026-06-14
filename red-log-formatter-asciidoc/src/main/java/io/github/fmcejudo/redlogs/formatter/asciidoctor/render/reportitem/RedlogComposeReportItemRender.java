package io.github.fmcejudo.redlogs.formatter.asciidoctor.render.reportitem;

import java.util.List;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.AsciiDetailsRender;
import io.github.fmcejudo.redlogs.report.domain.ReportItem;

public class RedlogComposeReportItemRender implements AsciiDetailsRender {

  private final List<AsciiDetailsRender> renderList;

  private RedlogComposeReportItemRender(AsciiDetailsRender... renders) {
    this.renderList = List.of(renders);
  }

  public static RedlogComposeReportItemRender of(AsciiDetailsRender... renders) {
    return new RedlogComposeReportItemRender(renders);
  }

  @Override
  public String content(ReportItem reportItem) {
    return renderList.stream().filter(r -> r.matching(reportItem)).findFirst().map(r -> r.content(reportItem)).orElse(null);
  }

  @Override
  public boolean matching(ReportItem reportItem) {
    return renderList.stream().anyMatch(r -> r.matching(reportItem));
  }
}
