package com.github.fmcejudo.redlogs.common.link;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

final class WebUrlLinkBuilder implements UrlLinkBuilder {

  private final HttpServletRequest httpServletRequest;

  private WebUrlLinkBuilder(HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }

  static WebUrlLinkBuilder from(final HttpServletRequest servletRequest) {
    return new WebUrlLinkBuilder(servletRequest);
  }

  public String build() {
    URI uri = URI.create(httpServletRequest.getRequestURI());
    final String origin;
    if (uri.getPort() > 0) {
      origin ="%s://%s:%d".formatted(uri.getScheme(), uri.getHost(), uri.getPort());
    } else {
      origin ="%s://%s".formatted(uri.getScheme(), uri.getHost());
    }

    if (StringUtils.isBlank(httpServletRequest.getContextPath())) {
      return origin;
    }

    return String.join("/", origin, httpServletRequest.getContextPath());
  }
}
