package com.github.fmcejudo.redlogs.card.process;

import com.github.fmcejudo.redlogs.card.model.CardType;
import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.instant.QueryInstantClient;
import com.github.fmcejudo.redlogs.client.loki.range.QueryRangeClient;
import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "redlog.loki.url=http://localhost:3100",
        "redlog.loki.username=username",
        "redlog.loki.password=password"
})
@ContextConfiguration(classes = {
        RedLogLokiConfig.class
})
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackageClasses = {RedLogLokiConfig.class})
class LokiClientFactoryTest {

    @Autowired
    private RedLogLokiConfig redLogLokiConfig;

    private LokiClientFactory lokiClientFactory;

    @BeforeEach
    void setUp() {
        this.lokiClientFactory = LokiClientFactory.createInstance(redLogLokiConfig);
    }

    @Test
    void shouldCreateRangeClientOnSummary() {
        //Given
        final CardType cardType = CardType.SUMMARY;

        //When
        LokiClient lokiClient = lokiClientFactory.get(cardType);

        //Then
        Assertions.assertThat(lokiClient).isInstanceOf(QueryRangeClient.class);
    }

    @Test
    void shouldCreateInstantClientOnCount() {
        //Given
        final CardType cardType = CardType.COUNT;

        //When
        LokiClient lokiClient = lokiClientFactory.get(cardType);

        //Then
        Assertions.assertThat(lokiClient).isInstanceOf(QueryInstantClient.class);
    }


}