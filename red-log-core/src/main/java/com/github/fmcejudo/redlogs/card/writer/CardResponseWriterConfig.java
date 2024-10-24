package com.github.fmcejudo.redlogs.card.writer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
class CardResponseWriterConfig {

    @Bean
    @ConditionalOnMissingBean({CardResponseWriter.class})
    CardResponseWriter cardResponseWriter(CardExecutionAppender cardExecutionAppender,
                                          CardReportAppender cardReportAppender) {
        return new DefaultCardResponseWriter(cardExecutionAppender, cardReportAppender);
    }
}
