package com.github.fmcejudo.redlogs.card.converter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CardConverterConfiguration {

    @Bean
    @ConditionalOnMissingBean(CardConverter.class)
    CardConverter cardConverter() {
        return new DefaultCardConverter();
    }
}
