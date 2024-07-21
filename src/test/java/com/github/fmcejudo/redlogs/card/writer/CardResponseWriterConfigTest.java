package com.github.fmcejudo.redlogs.card.writer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class CardResponseWriterConfigTest {


    @Test
    void shouldCreateCardResponseWriterBean() {
        //Given
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withBean(CardExecutionAppender.class, () -> execution -> {
                })
                .withBean(CardReportAppender.class, () -> reportSection -> {
                })
                .withConfiguration(AutoConfigurations.of(CardResponseWriterConfig.class));
        //When
        contextRunner.run(context -> {
            Assertions.assertThat(context).hasSingleBean(CardResponseWriter.class);
        });
        //Then
    }

}