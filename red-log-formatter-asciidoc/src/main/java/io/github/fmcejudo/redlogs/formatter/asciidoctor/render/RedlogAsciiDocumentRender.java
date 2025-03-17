package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.List;
import java.util.function.Function;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.exception.SectionRenderException;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;

public class RedlogAsciiDocumentRender implements AsciiDocumentRender {

  private static final String CSS_STYLE = """
      ++++
      <style>
      
      .tag-container {
        padding: 12px;
        border-radius: 8px;
        background: #f9f9f9;
        margin: 12px 0;
      }
      
      .tag {
        background-color: #ff6347;
        color: white;
        font-size: 0.5em;
        padding: 3px 8px;
        border-radius: 12px;
      }
      
      .shadow-box {
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 16px;
        margin: 20px 0;
        background: white;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      }
      
      .shadow-box p {
        margin: 0;
      }
      </style>
      ++++
      """;

  private static final String AUTHOR_NAME = "Redlog";

  private final RedlogDocumentContext context;

  private final RedlogAsciiConfig config;

  private List<AsciiSectionRender> sectionRenderList;

  private RedlogAsciiDocumentRender(RedlogAsciiConfig config) {
    this.config = config;
    this.context = new RedlogDocumentContext();
  }

  public static RedlogAsciiDocumentRender withConfig(RedlogAsciiConfig config) {
    return new RedlogAsciiDocumentRender(config);
  }

  public RedlogAsciiDocumentRender withTitle(Function<Report, String> titleFn) {
    this.context.setTitleFn(titleFn);
    return this;
  }

  public RedlogAsciiDocumentRender withSectionRenderList(final List<AsciiSectionRender> sectionRenderList) {
    this.sectionRenderList = sectionRenderList;
    return this;
  }

  public String render(Report report) {

    var writer = RedlogAsciiWriter.instance();

    writer.addContent("= " + context.titleFn().apply(report))
        .addContent(AUTHOR_NAME)
        .addContent(":icons: font")
        .addLineIf(":toc: left", config::hasContentTable)
        .addLineIf(":toc-title: Table Of Content", config::hasContentTable)
        .addLineIf(":sectnums:", config::hasPagination)
        .blankLine()
        .addContent(CSS_STYLE);

    report.sections().forEach(reportSection -> {
      AsciiSectionRender sectionRender = findFirstRenderer(reportSection);
      writer.blankLine().addContent(sectionRender.render(reportSection));
    });

    return writer.toString();
  }

  private AsciiSectionRender findFirstRenderer(final ReportSection reportSection) {
    return sectionRenderList.stream().filter(sr -> sr.match(reportSection))
        .findFirst()
        .orElseThrow(() -> new SectionRenderException("There is no matching render for section id " + reportSection.id()));
  }

  private static class RedlogDocumentContext {

    private Function<Report, String> titleFn;

    public Function<Report, String> titleFn() {
      return titleFn;
    }

    public RedlogDocumentContext setTitleFn(
        Function<Report, String> titleFn) {
      this.titleFn = titleFn;
      return this;
    }
  }

}

