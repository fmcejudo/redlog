package com.github.fmcejudo.redlogs.card.process;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CardProcessorConfiguration {

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(CardProcessor.class)
  CardProcessor cardProcessor() {
    return new DefaultCardProcessor();
  }

}
