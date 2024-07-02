package com.github.fmcejudo.redlogs.report;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = ReportController.class)
@ContextConfiguration(classes = {
        ReportController.class
})
class ReportControllerTest {

    private static final String CONTROLLER_PATH = "/report/";

    @MockBean
    ReportServiceProxy reportServiceProxy;

    @Autowired
    WebTestClient webTestClient;


    @Test
    void shouldCallReport() {
        //Given
        Mockito.doAnswer(a -> {

            ReportContext reportContext = a.getArgument(0);
            String applicationName = reportContext.applicationName();
            LocalDate reportDate = reportContext.reportDate();

            Assertions.assertThat(applicationName).isEqualTo("TEST");
            Assertions.assertThat(reportDate).isEqualTo(now().format(ISO_LOCAL_DATE));

            return null;
        }).when(reportServiceProxy).content(any(ReportContext.class));

        //When
        var response = webTestClient.get()
                .uri(uri -> uri.path(CONTROLLER_PATH.concat("{applicationName}"))
                        .queryParams(new LinkedMultiValueMap<>(Map.of(
                                "date", List.of(now().format(ISO_LOCAL_DATE)),
                                "environment", List.of("des")
                        )))
                        .build("TEST"))
                .accept(MediaType.APPLICATION_JSON).exchange();

        //Then
        response.expectStatus().isOk();

        Mockito.verify(reportServiceProxy, Mockito.times(1)).content(any(ReportContext.class));
    }

    @Test
    void shouldFindAllExecutions() {
        //Given

        //When
        ResponseSpec response = webTestClient.get().uri(u -> u.path(CONTROLLER_PATH.concat("/list")).build())
                .accept(MediaType.APPLICATION_JSON).exchange();


        //Then
        response.expectStatus().isOk().expectBodyList(ReportController.Execution.class);
    }

}