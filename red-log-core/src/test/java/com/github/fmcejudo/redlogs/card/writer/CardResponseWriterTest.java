package com.github.fmcejudo.redlogs.card.writer;

import io.github.fmcejudo.redlogs.card.domain.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.domain.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import com.github.fmcejudo.redlogs.execution.domain.Execution;
import com.github.fmcejudo.redlogs.report.domain.ReportSection;
import io.github.fmcejudo.redlogs.card.writer.CardResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        DefaultCardResponseWriter.class
})
class CardResponseWriterTest {

    @MockBean
    CardExecutionAppender cardExecutionAppender;

    @MockBean
    CardReportAppender cardReportAppender;

    @Autowired
    CardResponseWriter cardResponseWriter;

    @Test
    void shouldSaveReportData() {

        //Given
        final String application = "TEST";
        final String executionId = UUID.randomUUID().toString();
        Mockito.doAnswer(a -> {
            ReportSection reportSection = a.getArgument(0);
            Assertions.assertThat(reportSection).satisfies(r -> {
                Assertions.assertThat(r.executionId()).isEqualTo(executionId);
                Assertions.assertThat(r.items()).hasSize(1).first().satisfies(rs -> {
                    Assertions.assertThat(rs.labels()).containsEntry("host", "localhost");
                });
                Assertions.assertThat(r.reportId()).isEqualTo("reportId");
            });
            return null;
        }).when(cardReportAppender).add(any(ReportSection.class));

        CardQueryResponse cardQueryResponse = new CardQueryResponse(
                application, LocalDate.now(), "reportId", executionId, "reportId description",
                List.of(new CardQueryResponseEntry(Map.of("host", "localhost"), 10L)),
                "http://link", null);

        //When
        cardResponseWriter.onNext(cardQueryResponse);

        //Then
        Mockito.verify(cardReportAppender, Mockito.times(1)).add(any(ReportSection.class));

    }

    @Test
    void shouldSaveAnExecution() {
        //Given
        final String application = "TEST";
        final String executionId = UUID.randomUUID().toString();

        CardRequest cardRequest = new CardRequest(
                application, LocalDate.now(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now(), List.of(), Map.of()
        ).withExecutionId(executionId);

        Mockito.doAnswer(a -> {
            Execution execution = a.getArgument(0);
            Assertions.assertThat(execution.id()).isEqualTo(executionId);
            Assertions.assertThat(execution.application()).isEqualTo(application);
            return null;
        }).when(cardExecutionAppender).add(any(Execution.class));

        //When
        cardResponseWriter.writeExecution(cardRequest);

        //Then
        Mockito.verify(cardExecutionAppender, Mockito.times(1)).add(any(Execution.class));
    }


}