package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.fmcejudo.redlogs.report.domain.ReportItem;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;

public final class RedlogAsciiSectionRender {

  private final AsciiSectionContext context;

  private RedlogAsciiSectionRender() {
    this.context = new AsciiSectionContext();
  }

  public static RedlogAsciiSectionRender createSectionRender() {
    return new RedlogAsciiSectionRender();
  }

  public RedlogAsciiSectionRender withTitle(Function<ReportSection, String> titleFn) {
    this.context.setTitleFn(titleFn);
    return this;
  }

  public RedlogAsciiSectionRender whenMatching(Predicate<ReportSection> useRenderPredicate) {
    this.context.setUseRenderPredicate(useRenderPredicate);
    return this;
  }

  public RedlogAsciiSectionRender withDetailsRender(RedlogAsciiDetailsRender detailsRender) {
    this.context.addDetailsRender(detailsRender);
    return this;
  }

  public String render(ReportSection reportSection) {

    var writer = RedlogAsciiWriter.instance();
    writer.addContent("== " + context.titleFn().apply(reportSection) + " [.tag]#important#").blankLine();

    for (ReportItem item : reportSection.items()) {
      RedlogAsciiDetailsRender detailsRender = this.context.detailsRenderList.getFirst();
      writer.addContent(detailsRender.content(item)).blankLine();
    }

    return writer.toString();
  }

  public boolean match(ReportSection reportSection) {
    return context.useRenderPredicate().test(reportSection);
  }


  private static class AsciiSectionContext {

    private Function<ReportSection, String> titleFn;

    private Predicate<ReportSection> useRenderPredicate = r -> true;

    private final List<RedlogAsciiDetailsRender> detailsRenderList = new ArrayList<>();

    public Function<ReportSection, String> titleFn() {
      return titleFn;
    }

    public AsciiSectionContext setTitleFn(
        Function<ReportSection, String> titleFn) {
      this.titleFn = titleFn;
      return this;
    }

    public Predicate<ReportSection> useRenderPredicate() {
      return useRenderPredicate;
    }

    public AsciiSectionContext setUseRenderPredicate(
        Predicate<ReportSection> useRenderPredicate) {
      this.useRenderPredicate = useRenderPredicate;
      return this;
    }

    public AsciiSectionContext addDetailsRender(RedlogAsciiDetailsRender detailsRender) {
      detailsRenderList.add(detailsRender);
      return this;
    }
  }
}

