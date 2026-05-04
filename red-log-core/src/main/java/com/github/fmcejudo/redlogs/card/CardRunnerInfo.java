package com.github.fmcejudo.redlogs.card;

import java.util.Map;

public record CardRunnerInfo(String applicationName, String uri, String executionId, String error, Map<String, String> params) {

  static CardRunnerInfo success(String applicationName, String uri, String executionId, Map<String, String> params) {
    return new CardRunnerInfo(applicationName, uri, executionId, null, params);
  }

  static CardRunnerInfo failure(String applicationName, String message, Map<String, String> params) {
    return new CardRunnerInfo(applicationName, null, null, message, params);
  }
}
