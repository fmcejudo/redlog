package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.loader.GithubCardLoader.DefaultGithubClient;
import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import org.apache.commons.lang3.StringUtils;
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
        String githubToken = redLogConfigProperties.getSource().getGithub().getGithubToken();
        if (StringUtils.isBlank(githubToken)) {
            throw new IllegalArgumentException("github token has not been defined within the github configuration");
        }
        var githubClient = new DefaultGithubClient(githubToken);
        return new GithubCardLoader(redLogConfigProperties.getSource().getGithub(), githubClient);
    }

    @Bean
    @ConditionalOnRedlogEnabled
    @ConditionalOnMissingBean(CardFileLoader.class)
    @ConditionalOnProperty(name = "redlog.source.type", havingValue = "FILE")
    public CardFileLoader fileCardLoader(final RedLogConfigProperties redLogConfigProperties) {
        return new FileCardLoader(redLogConfigProperties.getSource().getFile());
    }
}
