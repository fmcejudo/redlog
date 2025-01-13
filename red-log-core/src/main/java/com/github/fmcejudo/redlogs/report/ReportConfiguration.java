package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorContent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorFormat;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.report.ReportService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ReportConfiguration {

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(AsciiDoctorContent.class)
  AsciiDoctorContent asciiDoctorContent() {
    return reports -> "content";
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnMissingBean(DocumentFormat.class)
  DocumentFormat documentFormat(final AsciiDoctorContent asciiDoctorContent) {
    return new AsciiDoctorFormat(asciiDoctorContent);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  ReportReaderService reportServiceProxy(final ReportService reportService, final DocumentFormat documentFormat) {
    return new DefaultReportReaderService(reportService, documentFormat);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(ReportReaderService.class)
  ReportController reportController(final ReportReaderService reportServiceProxy) {
    return new ReportController(reportServiceProxy);
  }

}
