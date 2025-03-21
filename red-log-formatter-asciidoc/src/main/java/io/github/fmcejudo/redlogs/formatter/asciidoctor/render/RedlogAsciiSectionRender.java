package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.fmcejudo.redlogs.report.domain.ReportItem;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public final class RedlogAsciiSectionRender implements AsciiSectionRender {

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

  public RedlogAsciiSectionRender withDetailsRender(AsciiDetailsRender detailsRender) {
    this.context.addDetailsRender(detailsRender);
    return this;
  }

  @Override
  public String render(ReportSection reportSection) {

    var writer = RedlogAsciiWriter.instance();
    writer.addContent("== " + context.titleFn().apply(reportSection)).blankLine();

    if (StringUtils.isNotBlank(reportSection.link())) {
      writer
          .addContent("// section link")
          .addContent("link:++%s++[%s, window=\"_blank\"]".formatted(reportSection.link(), "link")).blankLine();
    }

    if (!CollectionUtils.isEmpty(reportSection.tags())) {
      writer.blankLine().addContent("[.tag-container]").addContent("--");
      reportSection.tags().forEach(t -> writer.addContent("[.tag]#" + t + "# "));
      writer.addContent("--");
    }

    for (ReportItem item : reportSection.items()) {
      AsciiDetailsRender detailsRender = this.context.detailsRenderList.getFirst();
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

    private final List<AsciiDetailsRender> detailsRenderList = new ArrayList<>();

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

    public AsciiSectionContext addDetailsRender(AsciiDetailsRender detailsRender) {
      detailsRenderList.add(detailsRender);
      return this;
    }
  }
}

