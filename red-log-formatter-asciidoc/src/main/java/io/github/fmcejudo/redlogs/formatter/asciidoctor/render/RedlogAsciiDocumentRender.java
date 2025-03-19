package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.github.fmcejudo.redlogs.formatter.asciidoctor.exception.SectionRenderException;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;

public class RedlogAsciiDocumentRender implements AsciiDocumentRender {

  private static final String CSS_STYLE = """
      ++++
      <style>
      /* Importing a clean modern font */
      @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap');
      
      /* Base document styling */
      body {
        font-family: 'Inter', sans-serif;
        color: #333;
        background-color: white;
        line-height: 1.6;
        font-size: 15px;
        padding: 20px;
      }
      
      /* Headings */
      h1, h2, h3, h4 {
        color: #222;
        font-weight: 600;
        margin: 1.5em 0 0.75em;
      }
      
      h1 {
        font-size: 2.8em;
        text-align: center;
        border-bottom: 3px solid #ccc;
        padding-bottom: 0.3em;
      }
      
      h2 {
        font-size: 1.5em;
        border-bottom: 2px solid #ddd;
        padding-bottom: 0.2em;
      }
      
      h3 {
        font-size: 1.2em;
      }
      
      .tag-container {
        padding: 8px 0;
        margin: 16px 0;
        display: flex;
        gap: 6px;
        flex-wrap: wrap;
      }
      
      .tag {
        background-color: #e6e6e6;
        color: #333;
        font-size: 0.75em;
        padding: 4px 10px;
        border-radius: 12px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.3px;
      }
      
      .shadow-box {
        border: 1px solid #dcdcdc;
        border-radius: 8px;
        padding: 16px;
        margin: 20px 0;
        background: #fafafa;
      }
      
      .cover-table {
        width: 60%;
        margin: 0 auto;
        border-collapse: collapse;
      }
      
      .cover-table th,
      .cover-table td {
        padding: 10px;
        border-bottom: 1px solid #ccc;
        text-align: left;
      }
      
      .cover-table th {
        font-weight: bold;
        background-color: #f2f2f2;
      }
      
      .shadow-box table {
        border-collapse: collapse;
        width: 100%;
        margin: 0.5em 0;
      }
      
      .shadow-box td, .shadow-box th {
        padding: 8px 10px;
        vertical-align: top;
      }
      
      .shadow-box table tr:nth-child(even) {
        background: #f2f2f2;
      }
      
      .shadow-box th {
        background: #e0e0e0;
        font-weight: 600;
      }
      
      .shadow-box p, .shadow-box dl {
        margin: 0.5em 0 0;
        font-size: 0.9em;
        color: #555;
      }
      
      a {
        color: #0066cc;
        text-decoration: none;
      }
      
      a:hover {
        text-decoration: underline;
      }
      
     
      #toctitle {
        font-family: 'Inter', sans-serif;
        color: black;
        font-weight: bold;
        font-size: 1.4em;
        text-align: center;
      }
      
      #toc {
        background: white;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
      }
      
      #toc a {
        color: black;
        text-decoration: none;
        font-size: 1em;
        padding: 6px 8px;
      }
      
      #toc ul {
        list-style: none;
        padding-left: 10px;
      }
      
      #toc ul li {
        margin: 8px 0;
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

    writer.addContent(renderCoverage(report.reportDate(), report.params())).blankLine();

    report.sections().forEach(reportSection -> {
      AsciiSectionRender sectionRender = findFirstRenderer(reportSection);
      writer.blankLine().addContent(sectionRender.render(reportSection));
    });

    return writer.toString();
  }

  private String renderCoverage(LocalDate reportDate, Map<String, String> params) {

    RedlogAsciiWriter coverage = RedlogAsciiWriter.instance();
    coverage.addContent("[.coverage-table]")
        .addContent("|===")
        .addContent("| Parameter | Value")
        .blankLine()
        .addContent("| Date Report ").addContent("| *" + reportDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "*")
        .blankLine();
    params.forEach((key, value) ->
        coverage.addContent("| " + key).addContent("| " + value).blankLine());

    coverage.addContent("|===").addContent("<<<");
    return coverage.toString();

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

