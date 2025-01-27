package io.github.fmcejudo.redlogs.healthcheck;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.RedlogPluginProvider;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import io.github.fmcejudo.redlogs.healthcheck.card.HealthCheckQueryRequest;
import io.github.fmcejudo.redlogs.healthcheck.processor.HealthCheckQueryProcessor;

public class HealthCheckRedlogPluginProvider implements RedlogPluginProvider {

  @Override
  public CardQueryProcessor createProcessor(Map<String, String> connectionDetails) {
    return HealthCheckQueryProcessor.createInstance();
  }

  @Override
  public CardQueryConverter createCardQueryConverter() {
    return HealthCheckQueryRequest::new;
  }

  @Override
  public CardQueryValidator createCardQueryValidator() {
    return c -> {
    };
  }

  @Override
  public String type() {
    return "HEALTHCHECK";
  }
}
