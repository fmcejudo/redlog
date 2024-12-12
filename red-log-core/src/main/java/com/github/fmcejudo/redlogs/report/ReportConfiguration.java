package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.card.CardController;
import com.github.fmcejudo.redlogs.card.CardRunner;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessorFactory;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorContent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorFormat;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
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
    return new ReportReaderService(reportService, documentFormat);
  }

  @Bean(destroyMethod = "close")
  @ConditionalOnRedlogEnabled
  CardRunner cardRunner(final CardLoader cardLoader,
      final CardProcessorFactory processorFactory,
      final CardExecutionWriter cardExecutionWriter,
      final CardReportWriter cardReportWriter) {
    return new CardRunner(cardLoader, processorFactory, cardExecutionWriter, cardReportWriter);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(CardRunner.class)
  CardController cardController(final CardRunner cardRunner) {
    return new CardController(cardRunner);
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnBean(ReportReaderService.class)
  ReportController reportController(final ReportReaderService reportServiceProxy) {
    return new ReportController(reportServiceProxy);
  }

}
