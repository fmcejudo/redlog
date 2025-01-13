package com.github.fmcejudo.redlogs.card;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public record CardRunnerInfo(String applicationName, String uri, String executionId, String error, Map<String, String> params) {

  static CardRunnerInfo success(String applicationName, String uri, String executionId, Map<String, String> params) {
    return new CardRunnerInfo(applicationName, uri, executionId, null, params);
  }

  static CardRunnerInfo failure(String applicationName, String message, Map<String, String> params) {
    return new CardRunnerInfo(applicationName, null, null, message, params);
  }
}
