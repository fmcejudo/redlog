package com.github.fmcejudo.redlogs.card.loader;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({RedLogConfigProperties.class})
class CardConfiguration {

    @Bean
    @ConditionalOnRedlogEnabled
    @ConditionalOnMissingBean(CardLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "GITHUB")
    public CardLoader githubCardLoader(final RedLogConfigProperties redLogConfigProperties,
                                       final CardConverter cardConverter) {
        return new GithubCardLoader(redLogConfigProperties.getSource().getGithub(), cardConverter);
    }

    @Bean
    @ConditionalOnRedlogEnabled
    @ConditionalOnMissingBean(CardLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "FILE")
    public CardLoader fileCardLoader(final RedLogConfigProperties redLogConfigProperties,
                                     final CardConverter cardConverter) {
        return new FileCardLoader(redLogConfigProperties.getSource().getFile(), cardConverter);
    }
}
