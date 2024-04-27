package com.github.fmcejudo.redlogs.engine.card.converter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardConverterConfig {

    @Bean
    @ConditionalOnMissingBean(CardConverter.class)
    CardConverter cardConverter() {
        return new DefaultCardConverter();
    }
}
