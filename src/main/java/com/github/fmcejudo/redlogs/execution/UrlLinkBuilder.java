package com.github.fmcejudo.redlogs.execution;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.URI;

final class UrlLinkBuilder {

    private final ServerHttpRequest exchange;

    private UrlLinkBuilder(final ServerHttpRequest exchange) {
        this.exchange = exchange;
    }

    static UrlLinkBuilder from(ServerHttpRequest request) {
        return new UrlLinkBuilder(request);
    }

    public String build() {

        URI uri = exchange.getURI();
        String urlBase = buildUrlBase(uri);
        String contextPath = extractContextPath(exchange);
        if (StringUtils.isNotBlank(contextPath)) {
            return String.join("/", urlBase, contextPath);
        }
        return urlBase;
    }

    private String buildUrlBase(URI uri) {
        StringBuilder urlBaseBuilder = new StringBuilder();
        urlBaseBuilder.append(uri.getScheme()).append("://").append(uri.getHost());
        if (uri.getPort() > 0) {
            urlBaseBuilder.append(":").append(uri.getPort());
        }
        return urlBaseBuilder.toString();
    }

    private String extractContextPath(final ServerHttpRequest request) {

        String contextPath = request.getPath().contextPath().value();
        if (contextPath.startsWith("/")) {
            return contextPath.substring(1);
        }
        return contextPath;
    }

}
