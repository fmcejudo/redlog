import io.github.fmcejudo.redlogs.card.processor.CardProcessorProvider;
import io.github.fmcejudo.redlogs.processor.loki.LokiCardProcessorProvider;

module red.log.processor.loki {

  requires red.log.api;
  requires org.slf4j;
  requires spring.web;
  requires spring.webflux;
  requires reactor.netty.http;
  requires org.apache.logging.log4j;
  requires com.fasterxml.jackson.databind;

  exports io.github.fmcejudo.redlogs.processor.loki.instant to com.fasterxml.jackson.databind;
  exports io.github.fmcejudo.redlogs.processor.loki.range to com.fasterxml.jackson.databind;

  opens io.github.fmcejudo.redlogs.processor.loki.instant to com.fasterxml.jackson.databind;
  opens io.github.fmcejudo.redlogs.processor.loki.range to com.fasterxml.jackson.databind;

  provides CardProcessorProvider with LokiCardProcessorProvider;

}