package com.github.fmcejudo.redlogs.card;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.runner.CardRunner;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;

@WebMvcTest(controllers = WebCardController.class)
@ContextConfiguration(classes = {
    WebCardController.class
})
class WebCardControllerTest {

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

      return "20";
    }).when(cardRunner).onCardContext(any(CardContext.class));

    //When
    var response = webTestClient.get()
        .uri(uri -> uri
            .host("test")
            .port("80")
            .scheme("http")
            .path(CONTROLLER_PATH.concat("/{applicationName}"))
            .queryParams(new LinkedMultiValueMap<>(Map.of(
                "date", List.of(now().format(ISO_LOCAL_DATE)),
                "environment", List.of("des")
            )))
            .build("TEST"))
        .accept(MediaType.APPLICATION_JSON).exchange();

    //Then
    response.expectStatus().isOk().expectBody().json("""
        {
        "applicationName":"TEST","executionId":"20","uri":"http://test/report/execution/20/doc", "params":{"environment":"des","date":"%s"}
        }
        """.formatted(LocalDate.now().format(ISO_LOCAL_DATE)));

    Mockito.verify(cardRunner, Mockito.times(1)).onCardContext(any(CardContext.class));
  }

  @Test
  void shouldThrowExceptionOnFailure() {
    //Given
    Mockito.doAnswer(a -> {

      CardContext context = a.getArgument(0);
      Assertions.assertThat(context.applicationName()).isEqualTo("TEST");
      Assertions.assertThat(context.parameters()).containsEntry("environment", "des").doesNotContainKey("date");
      Assertions.assertThat(context.reportDate()).isEqualTo(now().format(ISO_LOCAL_DATE));

      throw new CardExecutionException("oh! an error!");
    }).when(cardRunner).onCardContext(any(CardContext.class));

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
    response.expectStatus().isBadRequest().expectBody().json("""
        {"applicationName":"TEST","error":"oh! an error!","params":{"date":"%s","environment":"des"}}
        """.formatted(LocalDate.now().format(ISO_LOCAL_DATE)));

    Mockito.verify(cardRunner, Mockito.times(1)).onCardContext(any(CardContext.class));
  }

}