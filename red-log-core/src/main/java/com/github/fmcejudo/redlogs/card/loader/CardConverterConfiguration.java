package com.github.fmcejudo.redlogs.card.loader;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnRedlogEnabled
public class CardConverterConfiguration {

    @Bean
    @ConditionalOnMissingBean(CardConverter.class)
    CardConverter cardConverter() {
        return new DefaultCardConverter();
    }
}
