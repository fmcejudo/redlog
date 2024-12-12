package com.github.fmcejudo.redlogs.card.process;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import io.github.fmcejudo.redlogs.card.processor.CardProcessor;
import io.github.fmcejudo.redlogs.card.processor.CardProcessorProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnRedlogEnabled
@EnableConfigurationProperties(RedLogConfigProperties.class)
class CardProcessorConfiguration {

  @Bean
  CardProcessorFactory cardProcessorFactory(final RedLogConfigProperties redLogConfigProperties) {
    var loader = ServiceLoader.load(CardProcessorProvider.class);
    var loaderIterator = loader.iterator();
    if (!loaderIterator.hasNext()) {
      throw new RuntimeException("There is no plugin to process cards");
    }
    Map<String, CardProcessor> cardProcessorMap = new HashMap<>();
    loaderIterator.forEachRemaining(cpp -> {
      cardProcessorMap.put(cpp.type(), cpp.createProcessor(redLogConfigProperties.getProcessor()));
    });
    return new CardProcessorFactory(Collections.unmodifiableMap(cardProcessorMap));
  }

}
