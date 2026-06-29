package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class CardConfigurationTest {

    @Test
    void shouldCreateAGithubCardLoader() {
        //Given && When && Then
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(RedLogConfigProperties.class, CardLoaderConfiguration.class))
                .withBean(CardConverter.class, () -> ((content, cardContext) -> null))
                .withPropertyValues(
                    "redlog.source.type=GITHUB",
                    "redlog.source.github.github-token=token",
                    "redlog.source.github.url-mapper.TEST=http://github.com/app"
                )
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(GithubCardLoader.class);
                });

    }

    @Test
    void shouldCreateAFileCardLoader() {
        //Given && When && Then
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(RedLogConfigProperties.class, CardLoaderConfiguration.class))
                .withBean(CardConverter.class, () -> ((content, cardContext) -> null))
                .withPropertyValues("redlog.source.type=FILE", "redlog.source.file.files-path=/tmp")
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(FileCardLoader.class);
                });
    }


}
