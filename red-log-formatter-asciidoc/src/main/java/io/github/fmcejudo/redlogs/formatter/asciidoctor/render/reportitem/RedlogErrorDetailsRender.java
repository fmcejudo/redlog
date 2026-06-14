package io.github.fmcejudo.redlogs.formatter.asciidoctor.render.reportitem;

import java.util.Map;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.AsciiDetailsRender;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiWriter;
import io.github.fmcejudo.redlogs.report.domain.ReportItem;

public class RedlogErrorDetailsRender implements AsciiDetailsRender {

  public static RedlogErrorDetailsRender createDetailsRender() {
    return new RedlogErrorDetailsRender();
  }

  @Override
  public String content(ReportItem reportItem) {
    var writer = RedlogAsciiWriter.instance()
        .addContent("[.shadow-box]")
        .addContent("--");

    writer.addContent("""
        [WARNING]
        ====
        *error*: Result Report Item caused an error
        ====
        [cols="1,2"]
        |===
        |error
        |%s
        |===
        """.formatted(reportItem.labels().get("error")));

    writer.addContent("--");
    return writer.toString();
  }

  @Override
  public boolean matching(ReportItem reportItem) {
    return reportItem.labels().containsKey("error");
  }
}
