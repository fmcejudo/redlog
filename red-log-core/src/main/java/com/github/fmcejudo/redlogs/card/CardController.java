package com.github.fmcejudo.redlogs.card;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.runner.CardRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${redlog.card.controller-path:card-runner}")
public class CardController {

  private final CardRunner cardRunner;

  public CardController(final CardRunner cardRunner) {
    this.cardRunner = cardRunner;
  }

  @GetMapping("/{applicationName}")
  public ResponseEntity<CardRunnerInfo> triggerReport(@PathVariable String applicationName,
      @RequestParam(required = false) Map<String, String> params) {

    try {
      CardContext cardContext = CardContext.from(applicationName, params);
      String executionId = cardRunner.onCardContext(cardContext);
      return ResponseEntity.ok(CardRunnerInfo.success(applicationName, executionId, params));
    } catch (CardExecutionException cardExecutionException) {
      return ResponseEntity.badRequest().body(CardRunnerInfo.failure(applicationName, cardExecutionException.getMessage(), params));
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(Include.NON_NULL)
  public record CardRunnerInfo(String applicationName, String executionId, String error, Map<String, String> params) {

    static CardRunnerInfo success(String applicationName, String executionId, Map<String, String> params) {
      return new CardRunnerInfo(applicationName, executionId, null, params);
    }

    static CardRunnerInfo failure(String applicationName, String message, Map<String, String> params) {
      return new CardRunnerInfo(applicationName, null, message, params);
    }
  }
}
