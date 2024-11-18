package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({RedLogGithubProperties.class,RedLogFileProperties.class, RedLogMongoProperties.class})
class CardConfiguration {

    @Bean
    @ConditionalOnMissingBean(CardLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "GITHUB")
    public CardLoader githubCardLoader(final RedLogConfigProperties redLogConfigProperties,
                                       final CardConverter cardConverter) {
        return new GithubCardLoader(redLogConfigProperties.getSource().getGithub(), cardConverter);
    }

    @Bean
    @ConditionalOnMissingBean(CardLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "FILE")
    public CardLoader fileCardLoader(final RedLogConfigProperties redLogConfigProperties,
                                     final CardConverter cardConverter) {
        return new FileCardLoader(redLogConfigProperties.getSource().getFile(), cardConverter);
    }
}
