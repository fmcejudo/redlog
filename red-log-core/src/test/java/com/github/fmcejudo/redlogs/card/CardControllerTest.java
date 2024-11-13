package com.github.fmcejudo.redlogs.card;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

@WebFluxTest(controllers = CardController.class)
@ContextConfiguration(classes = {
        CardController.class
})
class CardControllerTest {

    private static final String CONTROLLER_PATH = "/card-runner";

    @MockBean
    CardRunner cardRunner;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldTriggerReport() {
        //Given
        Mockito.doAnswer(a -> {

            CardContext context = a.getArgument(0);
            Assertions.assertThat(context.applicationName()).isEqualTo("TEST");
            Assertions.assertThat(context.parameters()).containsEntry("environment", "des").doesNotContainKey("date");
            Assertions.assertThat(context.reportDate()).isEqualTo(now().format(ISO_LOCAL_DATE));

            return null;
        }).when(cardRunner).run(any(CardContext.class));

        //When
        var response = webTestClient.get()
                .uri(uri -> uri.path(CONTROLLER_PATH.concat("/{applicationName}"))
                        .queryParams(new LinkedMultiValueMap<>(Map.of(
                                "date", List.of(now().format(ISO_LOCAL_DATE)),
                                "environment", List.of("des")
                        )))
                        .build("TEST"))
                .accept(MediaType.APPLICATION_JSON).exchange();

        //Then
        response.expectStatus().isOk();

        Mockito.verify(cardRunner, Mockito.times(1)).run(any(CardContext.class));
    }

}