package com.github.fmcejudo.redlogs.execution;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

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
        Assertions.assertThat(link).isEqualTo(urlBase+"/"+contextPath);
    }

}