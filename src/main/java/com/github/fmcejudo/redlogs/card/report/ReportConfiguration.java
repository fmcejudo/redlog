package com.github.fmcejudo.redlogs.card.report;

import com.github.fmcejudo.redlogs.card.report.asciidoctor.AsciiDoctorContent;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.card.engine.CardExecutionService;
import com.github.fmcejudo.redlogs.card.engine.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.engine.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.engine.writer.CardResponseWriter;
import com.github.fmcejudo.redlogs.card.report.asciidoctor.AsciiDoctorReportService;
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
        return new ReportRepository(mongoTemplate, redLogMongoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(AsciiDoctorContent.class)
    AsciiDoctorContent asciiDoctorContent() {
        return reports -> "content";
    }

    @Bean
    @ConditionalOnMissingBean(ReportService.class)
    AsciiDoctorReportService reportService(final AsciiDoctorContent asciiDoctorContent) {
        return new AsciiDoctorReportService(asciiDoctorContent);
    }

    @Bean
    ReportServiceProxy reportServiceProxy(final ReportRepository reportRepository, final ReportService reportService) {
        return new ReportServiceProxy(reportRepository, reportService);
    }

    @Bean
    @ConditionalOnBean(value = {
            CardLoader.class, CardProcessor.class, CardResponseWriter.class
    })
    CardExecutionService cardExecutionService(final CardLoader cardLoader,
                                              final CardProcessor processor,
                                              final CardResponseWriter writer) {
        return new CardExecutionService(cardLoader, processor, writer);
    }

    @Bean
    ReportController reportController(ReportServiceProxy reportServiceProxy,
                                      CardExecutionService cardExecutionService) {
        return new ReportController(reportServiceProxy, cardExecutionService);
    }
}
