package com.github.fmcejudo.redlogs.report;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.report.ReportService;
import io.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ReportConfiguration {

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(DocumentFormat.class)
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
