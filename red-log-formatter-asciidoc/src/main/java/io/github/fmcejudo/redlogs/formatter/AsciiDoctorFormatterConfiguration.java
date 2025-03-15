package io.github.fmcejudo.redlogs.formatter;

import io.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class AsciiDoctorFormatterConfiguration {

  @Bean
  @ConditionalOnMissingBean(value = DocumentFormat.class)
  DocumentFormat documentFormat() {
    return new AsciiDoctorDocumentFormat();
  }
}
