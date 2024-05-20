package com.github.fmcejudo.redlogs.engine.card.process;

import com.github.fmcejudo.redlogs.client.loki.DefaultLokiClient;
import com.github.fmcejudo.redlogs.client.loki.LokiClient;
import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(RedLogLokiConfig.class)
class CardProcessorConfiguration {

    @Bean
    LokiClient lokiClient(final RedLogLokiConfig lokiConfig) {
        return new DefaultLokiClient(lokiConfig);
    }

    @Bean
    CardProcessor cardProcessor(final LokiClient lokiClient) {
        return new LokiCardProcessor(lokiClient);
    }
}
