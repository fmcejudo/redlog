package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({RedLogConfigProperties.class})
class CardLoaderConfiguration {

    @Bean
    @ConditionalOnRedlogEnabled
    @ConditionalOnMissingBean(CardFileLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "GITHUB")
    public CardFileLoader githubCardLoader(final RedLogConfigProperties redLogConfigProperties) {
        return new GithubCardLoader(redLogConfigProperties.getSource().getGithub());
    }

    @Bean
    @ConditionalOnRedlogEnabled
    @ConditionalOnMissingBean(CardFileLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "FILE")
    public CardFileLoader fileCardLoader(final RedLogConfigProperties redLogConfigProperties) {
        return new FileCardLoader(redLogConfigProperties.getSource().getFile());
    }
}
