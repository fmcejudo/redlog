package com.github.fmcejudo.redlogs.card.exception;

import java.util.Collection;
import java.util.List;

public class ReplacementException extends RuntimeException {

  public ReplacementException(String message) {
    super(message);
  }

  public ReplacementException(Collection<String> variables) {
    super(buildMessage(variables.stream().distinct().toList()));
  }

  public static String buildMessage(List<String> variables) {
    if (variables.isEmpty()) {
      throw new RuntimeException("List of variables is empty and this means there is no errors");
    }
    if (variables.size() == 1) {
      return "parameter '" + variables.getFirst() + "' has not been found";
    }
    StringBuilder joiningVariables = new StringBuilder();
    for (int i = 0; i < variables.size() - 1; i++) {
      joiningVariables.append("'").append(variables.get(i)).append("' ");
    }
    joiningVariables.append("and ").append("'").append(variables.getLast()).append("'");

    return "parameters " + joiningVariables.toString() + " have not been found";
  }
}
