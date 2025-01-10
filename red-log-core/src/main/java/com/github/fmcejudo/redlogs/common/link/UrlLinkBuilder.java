package com.github.fmcejudo.redlogs.common.link;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

@FunctionalInterface
public interface UrlLinkBuilder {

  String build();

  static UrlLinkBuilder from(final ServerHttpRequest exchange) {
    return ReactiveUrlLinkBuilder.from(exchange);
  }

  static UrlLinkBuilder from(final HttpServletRequest httpServletRequest) {
    return WebUrlLinkBuilder.from(httpServletRequest);
  }

  public default UrlLinkBuilder withPath(String path) {
    return () -> {
      String usedPath = path;
      String urlBase = this.build();
      if (urlBase.endsWith("/")) {
        urlBase = urlBase.substring(0, urlBase.length() - 1);
      }
      if (usedPath.endsWith("/")) {
        usedPath = usedPath.substring(0, usedPath.length() - 1);
      }
      if (usedPath.startsWith("/")) {
        usedPath = usedPath.substring(1);
      }
      return String.join("/", urlBase, usedPath);
    };
  }
}

