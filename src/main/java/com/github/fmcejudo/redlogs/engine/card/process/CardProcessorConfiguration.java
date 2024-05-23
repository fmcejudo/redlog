package com.github.fmcejudo.redlogs.engine.card.process;

import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(RedLogLokiConfig.class)
class CardProcessorConfiguration {

    @Bean
    CardProcessor cardProcessor(final RedLogLokiConfig redLogLokiConfig) {
        return new LokiCardProcessor(redLogLokiConfig);
    }
}
