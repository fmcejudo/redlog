package com.github.fmcejudo.redlogs.engine.card.process;

import com.github.fmcejudo.redlogs.client.loki.DefaultLokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LokiConfig.class)
class CardProcessorConfiguration {

    @Bean
    LokiClient lokiClient(final LokiConfig lokiConfig) {
        return new DefaultLokiClient(lokiConfig);
    }

    @Bean
    CardProcessor cardProcessor(final LokiClient lokiClient) {
        return new LokiCardProcessor(lokiClient);
    }
}
