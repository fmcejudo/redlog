package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.github.fmcejudo.redlogs.report.domain.ReportItem;

public class RedlogAsciiDetailsRender implements AsciiDetailsRender {

  private RedlogAsciiDetailsRender() {
  }

  public static RedlogAsciiDetailsRender createDetailsRender() {
    return new RedlogAsciiDetailsRender();
  }

  @Override
  public String content(ReportItem reportItem) {
    var writer = RedlogAsciiWriter.instance()
        .addContent("[.shadow-box]")
        .addContent("--");

    Map<String, String> shortLabels = shortLabels(reportItem.labels());
    Map<String, String> longLabels = longLabels(reportItem.labels());

    renderShortLabels(reportItem, writer, shortLabels);
    if (!longLabels.isEmpty()) {
      renderLongLabels(longLabels, writer);
    }

    writer.addContent("--");
    return writer.toString();
  }

  private Map<String, String> shortLabels(final Map<String, String> labels) {
    return labels.entrySet().stream()
        .filter(l -> l.getValue().length() < 200)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  private Map<String, String> longLabels(final Map<String, String> labels) {
    return labels.entrySet().stream()
        .filter(l -> l.getValue().length() >= 200)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  private void renderShortLabels(ReportItem reportItem, RedlogAsciiWriter writer, Map<String, String> tableMessages) {
    writer
        .addContent("[cols=\"1,2\"]")
        .addContent("|===");

    tableMessages.forEach((key, value) ->
        writer.addContent("|" + key).addContent("|" + value).blankLine()
    );

    writer.addContent("|===")
        .blankLine()
        .addLineIf("*Count*:: " + reportItem.count(), () -> reportItem.count() > 0);
  }

  private void renderLongLabels(Map<String, String> longLabels, RedlogAsciiWriter writer) {
    longLabels.forEach((key, value) ->
        writer.blankLine()
            .addContent("*"+key+"*")
            .blankLine()
            .addContent("[source,text]")
            .addContent("----")
            .addContent(value)
            .addContent("----")
            .blankLine()
    );
  }

}
