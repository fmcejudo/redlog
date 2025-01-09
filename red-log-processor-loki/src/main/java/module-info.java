import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import io.github.fmcejudo.redlogs.loki.LokiRedlogPluginProvider;

module red.log.processor.loki {

  requires red.log.api;
  requires org.slf4j;
  requires spring.web;
  requires spring.webflux;
  requires reactor.netty.http;
  requires org.apache.logging.log4j;
  requires com.fasterxml.jackson.databind;
  requires org.apache.commons.text;
  requires spring.core;
  requires org.apache.commons.lang3;
  requires org.yaml.snakeyaml;

  exports io.github.fmcejudo.redlogs.loki.processor.connection.instant to com.fasterxml.jackson.databind;
  exports io.github.fmcejudo.redlogs.loki.processor.connection.range to com.fasterxml.jackson.databind;

  opens io.github.fmcejudo.redlogs.loki.processor.connection.instant to com.fasterxml.jackson.databind;
  opens io.github.fmcejudo.redlogs.loki.processor.connection.range to com.fasterxml.jackson.databind;
  opens io.github.fmcejudo.redlogs.loki.processor.connection to spring.core;
  opens io.github.fmcejudo.redlogs.loki.processor to spring.core;

  provides RedlogPluginProvider with LokiRedlogPluginProvider;

}