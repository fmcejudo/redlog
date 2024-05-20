package com.github.fmcejudo.redlogs.client.loki;

import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = RedLogLokiConfig.class)
@ContextConfiguration(classes = {
        DefaultLokiClient.class,
        RedLogLokiConfig.class
})
@Testcontainers
@TestPropertySource(properties = {
        "logging.level.reactor.netty.http.client=DEBUG"
})
@Disabled
class LokiClientTest {

    @Autowired
    LokiClient lokiClient;

    @Autowired
    RedLogLokiConfig lokiConfig;

    @Container
    static GenericContainer<?> lokiContainer = new GenericContainer<>(DockerImageName.parse("grafana/loki:2.9.5"))
            .withExposedPorts(3100);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(lokiContainer).join();
        System.out.println(getLokiUrl(lokiContainer));
        registry.add("loki.url", () -> getLokiUrl(lokiContainer));
        registry.add("loki.username", () -> "username");
        registry.add("loki.password", () -> "password");
    }

    @Test
    void shouldDefineLokiConnection() {

        //Given && When && Then
        Assertions.assertThat(lokiConfig)
                .extracting("url", "username", "password")
                .containsExactly(getLokiUrl(lokiContainer), "username", "password");

    }

    @Test
    void shouldCreateAClient() {
        //Given When Then
        Assertions.assertThat(lokiClient).isNotNull();
    }

    private static String getLokiUrl(final GenericContainer<?> lokiContainer) {
        return "http://%s:%d".formatted(lokiContainer.getHost(), lokiContainer.getFirstMappedPort());
    }

}

