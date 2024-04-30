package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.engine.card.CardExecutionService;
import com.github.fmcejudo.redlogs.engine.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.engine.card.loader.CardLoader;
import com.github.fmcejudo.redlogs.engine.card.process.CardProcessor;
import com.github.fmcejudo.redlogs.engine.card.writer.CardResponseWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration(after = MongoTemplate.class)
public class ReportConfiguration {

    @Bean
    ReportRepository reportRepository(final MongoTemplate mongoTemplate) {
        return new ReportRepository(mongoTemplate);
    }

    @Bean
    ReportService reportService(ReportRepository reportRepository) {
        return new AsciiDoctorReportService(reportRepository);
    }


    @Bean
    @ConditionalOnBean(value = {
            CardLoader.class, CardProcessor.class, CardResponseWriter.class, CardConverter.class
    })
    CardExecutionService cardExecutionService(final CardLoader cardLoader,
                                              final CardProcessor processor,
                                              final CardResponseWriter writer,
                                              final CardConverter cardConverter) {
        return new CardExecutionService(cardLoader, processor, writer, cardConverter);
    }

    @Bean
    ReportController reportController(ReportService reportService, CardExecutionService cardExecutionService) {
        return new ReportController(reportService, cardExecutionService);
    }
}
