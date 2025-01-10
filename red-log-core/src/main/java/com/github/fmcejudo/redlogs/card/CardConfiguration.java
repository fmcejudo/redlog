package com.github.fmcejudo.redlogs.card;

import com.github.fmcejudo.redlogs.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.card.loader.CardFileLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.runner.CardRunner;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@AutoConfiguration
class CardConfiguration {


  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnClass(value = Flux.class)
  @ConditionalOnBean(CardRunner.class)
  ReactiveCardController reactiveCardController(final CardRunner cardRunner) {
    return new ReactiveCardController(cardRunner);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(ReactiveCardController.class)
  @ConditionalOnBean(CardRunner.class)
  WebCardController webCardController(final CardRunner cardRunner) {
    return new WebCardController(cardRunner);
  }


  @Bean
  @ConditionalOnRedlogEnabled
  CardRunner cardRunner(final CardFileLoader cardFileLoader,
      final CardConverter cardConverter,
      final CardProcessor cardProcessor,
      final CardExecutionWriter cardExecutionWriter,
      final CardReportWriter cardReportWriter) {

    return CardRunner.load(cardFileLoader)
        .transform(cardConverter)
        .process(cardProcessor)
        .run(cardReportWriter, cardExecutionWriter);
  }


}
