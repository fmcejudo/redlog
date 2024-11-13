package com.github.fmcejudo.redlogs.report;

import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.report.domain.Report;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = ReportController.class)
@ContextConfiguration(classes = {
        ReportController.class
})
class ReportControllerTest {

    private static final String CONTROLLER_PATH = "/report/execution/";

    @MockBean
    ReportReaderService reportServiceProxy;

    @Autowired
    WebTestClient webTestClient;


    @Test
    void shouldCallReport() {
        //Given
        final String executionId = "id";

        Mockito.doAnswer(a -> {
            String id = a.getArgument(0);
            Assertions.assertThat(id).isEqualTo(executionId);
            return null;
        }).when(reportServiceProxy).asBinaryPDF(anyString());

        //When
        var response = webTestClient.get()
                .uri(uri -> uri.path(CONTROLLER_PATH.concat("{id}/doc")).build(executionId))
                .accept(MediaType.APPLICATION_JSON).exchange();

        //Then
        response.expectStatus().isOk();

        Mockito.verify(reportServiceProxy, Mockito.times(1)).asBinaryPDF(anyString());
    }

    @Test
    void shouldCallReportAsJson() {
        //Given
        final String executionId = "id";

        Mockito.doAnswer(a -> {
            String id = a.getArgument(0);
            Assertions.assertThat(id).isEqualTo(executionId);
            return new Report("app", LocalDate.now(), Map.of(), List.of());
        }).when(reportServiceProxy).asReport(anyString());


        //When
        var response = webTestClient.get()
                .uri(uri -> uri.path(CONTROLLER_PATH.concat("{id}/json")).build(executionId))
                .accept(MediaType.APPLICATION_JSON).exchange();

        //Then
        response.expectStatus().isOk();

        Mockito.verify(reportServiceProxy, Mockito.times(1)).asReport(anyString());
    }

}