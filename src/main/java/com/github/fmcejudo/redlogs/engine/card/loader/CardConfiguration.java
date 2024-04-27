package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CardConfiguration {

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
