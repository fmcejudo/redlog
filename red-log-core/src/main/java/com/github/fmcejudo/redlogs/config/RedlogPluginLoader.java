package com.github.fmcejudo.redlogs.config;

import java.util.ServiceLoader;

import com.github.fmcejudo.redlogs.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.card.process.CardProcessor;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(RedLogConfigProperties.class)
class RedlogPluginLoader {

  @Bean
  @ConditionalOnRedlogEnabled
  RedlogPluginLoadRunner loadPlugins(
      final RedLogConfigProperties redLogConfigProperties, final CardConverter cardConverter, CardProcessor cardProcessor) {

    return new RedlogPluginLoadRunner(redLogConfigProperties, cardConverter, cardProcessor);
  }

}

class RedlogPluginLoadRunner implements ApplicationRunner, AutoCloseable {

  private final CardConverter cardConverter;

  private final CardProcessor cardProcessor;

  private final RedLogConfigProperties redLogConfigProperties;

  private final ServiceLoader<RedlogPluginProvider> loader;

  public RedlogPluginLoadRunner(RedLogConfigProperties redLogConfigProperties, CardConverter cardConverter, CardProcessor cardProcessor) {
    this.cardConverter = cardConverter;
    this.cardProcessor = cardProcessor;
    this.redLogConfigProperties = redLogConfigProperties;
    this.loader = ServiceLoader.load(RedlogPluginProvider.class);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var loaderIterator = loader.iterator();
    if (!loaderIterator.hasNext()) {
      throw new RuntimeException("There is no plugin to process cards");
    }
    loaderIterator.forEachRemaining(cpp -> {
      cardProcessor.register(cpp.type(), cpp.createProcessor(redLogConfigProperties.getProcessor()));
      cardConverter.register(cpp.type(), cpp.createCardQueryConverter());
    });
  }

  @Override
  public void close() throws Exception {
    loader.iterator().forEachRemaining(rp -> cardConverter.deregister(rp.type()));
  }
}

