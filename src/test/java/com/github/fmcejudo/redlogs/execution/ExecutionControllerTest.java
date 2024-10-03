package com.github.fmcejudo.redlogs.execution;

import com.github.fmcejudo.redlogs.execution.domain.Execution;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ExecutionController.class
})
@WebFluxTest(controllers = ExecutionController.class)
class ExecutionControllerTest {

    private static final String CONTROLLER_PATH = "/execution";

    @MockBean
    ExecutionService executionService;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldFindAllExecutions() {
        //Given
        final String applicationName = "TEST";
        Mockito.when(executionService.findExecutionWithParameters(applicationName, Map.of())).thenReturn(List.of(
                new Execution("execution-id", applicationName, Map.of(), LocalDate.now())
        ));

        //When
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri(u -> u.path(CONTROLLER_PATH.concat("/list/TEST")).build())
                .accept(MediaType.APPLICATION_JSON).exchange();

        //Then
        response.expectStatus().isOk().expectBodyList(ExecutionDTO.class).hasSize(1).value(l -> {
            ExecutionDTO executionDTO = l.getFirst();
            Assertions.assertThat(executionDTO.getExecutionId()).isEqualTo("execution-id");
        });
    }

}