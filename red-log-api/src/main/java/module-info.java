module red.log.api {
  requires spring.boot.autoconfigure;
  exports io.github.fmcejudo.redlogs.card.domain;
  exports io.github.fmcejudo.redlogs.card.processor;
  exports io.github.fmcejudo.redlogs.card.processor.filter;
  exports io.github.fmcejudo.redlogs.card.writer;
  exports io.github.fmcejudo.redlogs.report.domain;
}