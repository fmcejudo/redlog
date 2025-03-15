package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.function.Supplier;

public interface RedlogAsciiWriter extends CharSequence {

  public static RedlogAsciiWriter instance() {
    return DefaultRedlogAsciiWriter.instance();
  }

  RedlogAsciiWriter addContent(String content);

  RedlogAsciiWriter addLineIf(String content, Supplier<Boolean> matchingSupplier);

  RedlogAsciiWriter blankLine();



}
