package io.github.fmcejudo.redlogs.formatter.asciidoctor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.AsciiDocumentRender;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.AsciiSectionRender;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiConfig;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiConfigBuilder;
import io.github.fmcejudo.redlogs.formatter.asciidoctor.render.RedlogAsciiDocumentRender;
import io.github.fmcejudo.redlogs.report.domain.Report;

public class RedlogAsciiDocument {

  private final RedlogAsciiConfig config;

  private final List<AsciiSectionRender> sectionRenderList;

  private Function<Report, String> titleFn;

  private boolean showEmptySections = true;

  private RedlogAsciiDocument(final RedlogAsciiConfig config) {
    this.config = config;
    this.titleFn = Report::applicationName;
    this.sectionRenderList = new ArrayList<>();
  }

  public static RedlogAsciiDocument config(Function<RedlogAsciiConfigBuilder, RedlogAsciiConfig> configBuilder) {
    RedlogAsciiConfig asciiConfig = configBuilder.apply(RedlogAsciiConfigBuilder.builder());
    return new RedlogAsciiDocument(asciiConfig);
  }

  public RedlogAsciiDocument withTitle(Function<Report, String> titleFn) {
    this.titleFn = titleFn;
    return this;
  }

  public RedlogAsciiDocument withShowEmptySections(final boolean showEmptySections) {
    this.showEmptySections = showEmptySections;
    return this;
  }

  public RedlogAsciiDocument withSectionRender(AsciiSectionRender render) {
    this.sectionRenderList.add(render);
    return this;
  }

  public String build(Report report) {
    RedlogAsciiDocumentRender documentRender = RedlogAsciiDocumentRender
        .withConfig(config)
        .withTitle(titleFn)
        .showEmptyReports(showEmptySections)
        .withSectionRenderList(sectionRenderList);

    return this.build(documentRender, report);
  }

  public String build(AsciiDocumentRender documentRender, Report report) {
    return documentRender.render(report);
  }

}

