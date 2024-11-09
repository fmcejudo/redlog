package com.github.fmcejudo.redlogs.card.process;

import java.util.Map;
import java.util.ServiceLoader;

import com.github.fmcejudo.redlogs.config.RedLogLokiConfig;
import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.processor.CardProcessorProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(RedLogLokiConfig.class)
class CardProcessorConfiguration {

  @Bean
  CardProcessor cardProcessor(final RedLogLokiConfig redLogLokiConfig) {
    var loader = ServiceLoader.load(CardProcessorProvider.class);
    var loaderIterator = loader.iterator();
    if (loaderIterator.hasNext()) {
      return loaderIterator.next().createProcessor(Map.of());
    }
    throw new RuntimeException("There is no plugin to process cards");
  }
}
