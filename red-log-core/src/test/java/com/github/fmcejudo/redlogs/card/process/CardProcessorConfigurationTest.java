package com.github.fmcejudo.redlogs.card.process;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@Disabled
class CardProcessorConfigurationTest {

  @Test
  void shouldLoadLokiClient() {
    // Given && When && Then
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(CardProcessorConfiguration.class))
        .withPropertyValues()
        .run(context -> {
          // Assertions.assertThat(context).hasSingleBean(LokiCardProcessor.class);
        });
  }

}