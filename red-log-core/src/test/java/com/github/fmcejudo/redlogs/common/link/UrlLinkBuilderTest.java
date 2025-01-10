package com.github.fmcejudo.redlogs.common.link;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class UrlLinkBuilderTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "http://localhost:8080",
      "http://localhost",
      "https://localhost:8080"
  })
  @DisplayName("it should build a link from request")
  void shouldBuildBaseUrl(final String urlBase) {
    //Given
    MockServerHttpRequest request = MockServerHttpRequest.get("%s/something/path".formatted(urlBase)).build();

    //When
    String link = UrlLinkBuilder.from(request).build();

    //Then
    Assertions.assertThat(link).isEqualTo(urlBase);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "http://localhost:8080",
      "http://localhost",
      "https://localhost:8080"
  })
  @DisplayName("it should build a link from request with context-path")
  void shouldBuildBaseUrlWithContextPath(final String urlBase) {
    //Given
    final String contextPath = "app";
    MockServerHttpRequest request = MockServerHttpRequest
        .get("%s/%s/something/path".formatted(urlBase, contextPath))
        .contextPath("/app").build();

    //When
    String link = UrlLinkBuilder.from(request).build();

    //Then
    Assertions.assertThat(link).isEqualTo(urlBase + "/" + contextPath);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "http://localhost:8080",
      "http://localhost",
      "https://localhost:8080"
  })
  @DisplayName("it should build a link from web request with context-path")
  void shouldBuildUrlFromWebRequest(final String urlBase) {
    //Given

    HttpServletRequest httpServletRequest = MockHttpServletRequestFactory.from(urlBase , null ,"/something/path");

    //When
    String link = UrlLinkBuilder.from(httpServletRequest).build();

    //Then
    Assertions.assertThat(link).isEqualTo(urlBase);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "http://localhost:8080",
      "http://localhost",
      "https://localhost:8080"
  })
  @DisplayName("it should build a link from web request with context-path")
  void shouldBuildUrlFromWebRequestWithContextPath(final String urlBase) {
    //Given
    final String contextPath = "app";

    MockHttpServletRequest httpServletRequest =
        MockHttpServletRequestFactory.from(urlBase, contextPath, "/%s/something/path".formatted(contextPath));

    //When
    String link = UrlLinkBuilder.from(httpServletRequest).build();

    //Then
    Assertions.assertThat(link).isEqualTo(urlBase + "/" + contextPath);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "http://localhost:8080;/report/",
      "http://localhost:8080;report/",
      "http://localhost:8080;/report",
      "http://localhost:8080/;/report"
  }, delimiter = ';')
  @DisplayName("it should build a link from web request with context-path plus a resource")
  void shouldBuildUrlWithResource(final String urlBase, final String resource) {
    //Given
    final String contextPath = "app";

    MockHttpServletRequest httpServletRequest =
        MockHttpServletRequestFactory.from(urlBase, contextPath, "/%s/something/path".formatted(contextPath));

    //When
    String link = UrlLinkBuilder.from(httpServletRequest).withPath(resource).build();

    //Then
    Assertions.assertThat(link).isEqualTo("%s/%s/report".formatted("http://localhost:8080", contextPath));
  }

  static class MockHttpServletRequestFactory {

    static MockHttpServletRequest from(String urlBase, String contextPath, String resource) {
      URI uri = URI.create(urlBase);
      MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("GET", resource);
      httpServletRequest.setRemoteHost(uri.getHost());
      httpServletRequest.setRemotePort(uri.getPort());
      httpServletRequest.setLocalPort(uri.getPort());
      httpServletRequest.setServerPort(uri.getPort());
      httpServletRequest.setScheme(uri.getScheme());
      if (contextPath != null) {
        httpServletRequest.setContextPath(contextPath);
      }
      return httpServletRequest;
    }
  }

}