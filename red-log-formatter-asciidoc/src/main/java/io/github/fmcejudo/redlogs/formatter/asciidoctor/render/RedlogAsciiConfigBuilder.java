package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.Map;

public class RedlogAsciiConfigBuilder {

  private boolean contentTable;

  private boolean pagination;

  public Map<String, String> metadata;

  private RedlogAsciiConfigBuilder() {
  }

  public static RedlogAsciiConfigBuilder builder() {
    return new RedlogAsciiConfigBuilder();
  }

  public RedlogAsciiConfigBuilder withContentTable() {
    this.contentTable = true;
    return this;
  }

  public RedlogAsciiConfigBuilder withPagination() {
    this.pagination = true;
    return this;
  }

  public RedlogAsciiConfigBuilder withMetadata(final Map<String, String> metadata) {
    this.metadata = metadata;
    return this;
  }

  public RedlogAsciiConfig withDefault() {
    this.contentTable = true;
    this.pagination = false;
    this.metadata = Map.of();
    return this.build();
  }

  public RedlogAsciiConfig build() {
    return new RedlogAsciiConfig(this.contentTable, this.pagination, this.metadata);
  }
}
