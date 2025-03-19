package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.function.Supplier;
import java.util.stream.IntStream;

class DefaultRedlogAsciiWriter implements RedlogAsciiWriter {

  private final StringBuilder sb;

  private DefaultRedlogAsciiWriter() {
    this.sb = new StringBuilder();
  }

  public static DefaultRedlogAsciiWriter instance() {
    return new DefaultRedlogAsciiWriter();
  }

  @Override
  public RedlogAsciiWriter addContent(String content) {
    sb.append(content).append("\r\n");
    return this;
  }

  @Override
  public RedlogAsciiWriter addLineIf(String content, Supplier<Boolean> matchingSupplier) {
    if (matchingSupplier.get()) {
      sb.append(content).append("\r\n");
    }
    return this;
  }

  @Override
  public RedlogAsciiWriter blankLine() {
    sb.append("\r\n");
    return this;
  }

  @Override
  public char charAt(int index) {
    return sb.charAt(index);
  }

  @Override
  public boolean isEmpty() {
    return sb.isEmpty();
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return sb.subSequence(start, end);
  }

  @Override
  public int length() {
    return sb.length();
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  @Override
  public IntStream chars() {
    return sb.chars();
  }

  @Override
  public IntStream codePoints() {
    return sb.codePoints();
  }

}
