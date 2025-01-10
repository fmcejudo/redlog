package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.card.ReactiveCardController;
import com.github.fmcejudo.redlogs.card.WebCardController;
import com.github.fmcejudo.redlogs.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.card.loader.CardFileLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.runner.CardRunner;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorContent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorFormat;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import io.github.fmcejudo.redlogs.report.ReportService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

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

}
