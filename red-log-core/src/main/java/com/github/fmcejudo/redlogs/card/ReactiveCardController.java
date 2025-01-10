package com.github.fmcejudo.redlogs.card;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.runner.CardRunner;
import com.github.fmcejudo.redlogs.common.link.UrlLinkBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/${redlog.card.controller-path:card-runner}")
public class ReactiveCardController {

  private final CardRunner cardRunner;

  @Value("${redlog.report.controller-path:report}")
  private String reportPath;

  public ReactiveCardController(final CardRunner cardRunner) {
    this.cardRunner = cardRunner;
  }

  @GetMapping("/{applicationName}")
  public ResponseEntity<CardRunnerInfo> triggerReport(@PathVariable String applicationName,
      @RequestParam(required = false) Map<String, String> params, ServerWebExchange exchange) {
    try {
      CardContext cardContext = CardContext.from(applicationName, params);
      String executionId = cardRunner.onCardContext(cardContext);
      String uri = UrlLinkBuilder.from(exchange.getRequest()).withPath(reportPath).withPath("execution").withPath(executionId).build();
      return ResponseEntity.ok(CardRunnerInfo.success(applicationName, uri, executionId, params));
    } catch (CardExecutionException cardExecutionException) {
      return ResponseEntity.badRequest().body(CardRunnerInfo.failure(applicationName, cardExecutionException.getMessage(), params));
    }
  }
}
