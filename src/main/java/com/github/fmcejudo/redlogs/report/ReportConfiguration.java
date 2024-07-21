package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.card.CardController;
import com.github.fmcejudo.redlogs.card.CardRunner;
import com.github.fmcejudo.redlogs.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.card.writer.CardReportAppender;
import com.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorContent;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorFormat;
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
    @ConditionalOnMissingBean(AsciiDoctorContent.class)
    AsciiDoctorContent asciiDoctorContent() {
        return reports -> "content";
    }

    @Bean
    @ConditionalOnMissingBean(DocumentFormat.class)
    DocumentFormat documentFormat(final AsciiDoctorContent asciiDoctorContent) {
        return new AsciiDoctorFormat(asciiDoctorContent);
    }

    @Bean
    @ConditionalOnMissingBean(ReportService.class)
    ReportService reportService(final MongoTemplate mongoTemplate,
                                final RedLogMongoProperties redLogMongoProperties) {
        return new MongoReportService(mongoTemplate, redLogMongoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(CardReportAppender.class)
    CardReportAppender cardReportAppender(final MongoTemplate mongoTemplate,
                                          final RedLogMongoProperties redLogMongoProperties) {
        return new MongoReportService(mongoTemplate, redLogMongoProperties);
    }

    @Bean
    ReportServiceProxy reportServiceProxy(final ReportService reportService,
                                          final DocumentFormat documentFormat) {
        return new ReportServiceProxy(reportService, documentFormat);
    }

    @Bean
    @ConditionalOnBean(value = {
            CardLoader.class, CardProcessor.class, CardResponseWriter.class
    })
    CardRunner cardRunner(final CardLoader cardLoader,
                          final CardProcessor processor,
                          final CardResponseWriter writer) {
        return new CardRunner(cardLoader, processor, writer);
    }

    @Bean
    CardController cardController(final CardRunner cardRunner) {
        return new CardController(cardRunner);
    }

    @Bean
    ReportController reportController(ReportServiceProxy reportServiceProxy) {
        return new ReportController(reportServiceProxy);
    }

}
