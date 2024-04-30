package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({RedLogGithubProperties.class,RedLogFileProperties.class})
class CardConfiguration {

    @Bean
    ApplicationRunner runner() {
        return args -> {
            System.out.println("initializing my card configuration");
        };
    }

    @Bean
    @ConditionalOnMissingBean(CardLoader.class)
    @ConditionalOnProperty(name = "redlog.source-type", havingValue = "GITHUB")
    public CardLoader githubCardLoader(RedLogGithubProperties redLogGithubProperties) {
        return new GithubCardLoader(redLogGithubProperties);
    }

    @Bean
    @ConditionalOnMissingBean(CardLoader.class)
    @ConditionalOnProperty(name = "redlog.source-type", havingValue = "FILE")
    public CardLoader fileCardLoader(RedLogFileProperties redLogFileProperties) {
        return new FileCardLoader(redLogFileProperties);
    }
}
