module red.log.api {
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires micrometer.commons;
  exports io.github.fmcejudo.redlogs.card;
  exports io.github.fmcejudo.redlogs.card.converter;
  exports io.github.fmcejudo.redlogs.card.validator;
  exports io.github.fmcejudo.redlogs.card.processor;
  exports io.github.fmcejudo.redlogs.card.writer;
  exports io.github.fmcejudo.redlogs.report.domain;
}