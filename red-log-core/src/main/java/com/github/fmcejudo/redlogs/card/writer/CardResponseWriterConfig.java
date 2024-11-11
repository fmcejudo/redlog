package com.github.fmcejudo.redlogs.card.writer;

import java.util.List;

import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
class CardResponseWriterConfig {

  @Bean
  @ConditionalOnMissingBean({CardReportWriter.class})
  CardReportWriter cardResponseWriter(List<CardReportWriter> reportWriters) {
    if (reportWriters == null || reportWriters.isEmpty()) {
      throw new IllegalStateException("application requires a " + CardReportWriter.class.getName());
    }
    if (reportWriters.size() == 1) {
      return reportWriters.getFirst();
    }
    throw new IllegalStateException("multiple " + CardReportWriter.class.getName() + "found in application and only one is needed");
  }

  @Bean
  @ConditionalOnMissingBean({CardExecutionWriter.class})
  CardExecutionWriter cardExecutionWriter(List<CardExecutionWriter> executionWriters) {
    if (executionWriters == null || executionWriters.isEmpty()) {
      throw new IllegalStateException("application requires a " + CardExecutionWriter.class.getName());
    }
    if (executionWriters.size() == 1) {
      return executionWriters.getFirst();
    }
    throw new IllegalStateException("multiple " + CardExecutionWriter.class.getName() + "found in application and only one is needed");
  }

}
