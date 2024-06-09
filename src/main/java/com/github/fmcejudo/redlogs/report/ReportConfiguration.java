package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.card.CardRunner;
import com.github.fmcejudo.redlogs.card.RedlogExecutionService;
import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorContent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorDocumentService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;


@ConfigurationPropertiesScan
@AutoConfiguration(after = MongoTemplate.class)
public class ReportConfiguration {

    @Bean
    ReportRepository reportRepository(final MongoTemplate mongoTemplate,
                                      final RedLogMongoProperties redLogMongoProperties) {
        return new ReportRepository();
    }

    @Bean
    @ConditionalOnMissingBean(AsciiDoctorContent.class)
    AsciiDoctorContent asciiDoctorContent() {
        return reports -> "content";
    }

    @Bean
    @ConditionalOnMissingBean(DocumentFormat.class)
    AsciiDoctorDocumentService reportService(final AsciiDoctorContent asciiDoctorContent) {
        return new AsciiDoctorDocumentService(asciiDoctorContent);
    }

    @Bean
    ReportServiceProxy reportServiceProxy(final ReportRepository reportRepository, final DocumentFormat reportService) {
        return new ReportServiceProxy(reportRepository, reportService);
    }

    @Bean
    @ConditionalOnBean(value = {
            CardLoader.class, CardProcessor.class, CardResponseWriter.class
    })
    CardRunner cardExecutionService(final CardLoader cardLoader,
                                    final CardProcessor processor,
                                    final CardResponseWriter writer,
                                    final RedlogExecutionService redlogExecutionService) {
        return new CardRunner(cardLoader, processor, writer, redlogExecutionService);
    }

    @Bean
    ReportController reportController(ReportServiceProxy reportServiceProxy) {
        return new ReportController(reportServiceProxy);
    }
}
