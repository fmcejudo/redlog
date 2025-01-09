package com.github.fmcejudo.redlogs.card.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ParameterReplacement {

  private final Logger logger = LoggerFactory.getLogger(ParameterReplacement.class);

  public String replace(String content, Map<String, String> parameters) {

    Set<String> parameterNames = parameters.keySet();
    StringBuilder builder = new StringBuilder();
    List<Replacement> listOfUnknownVariables = new ArrayList<>();
    for (int i = 0; i < content.length(); i++) {
      if(content.charAt(i) == '<') {
        Replacement replacement = searchParameterName(content, i + 1, parameterNames);
        if (replacement.needReplacement()) {
          String name = replacement.name();
          builder.append(parameters.get(name));
          i += replacement.advancePositions();
        } else {
          builder.append(content.charAt(i));
        }

        if (replacement instanceof UnknownReplacement unknownReplacement) {
          listOfUnknownVariables.add(unknownReplacement);
        }
      } else {
        builder.append(content.charAt(i));
      }
    }
    if (!listOfUnknownVariables.isEmpty()) {
      List<String> variables = listOfUnknownVariables.stream().map(Replacement::name).toList();
      logger.warn("There are some variables which have not been replaced, ensure they are correct: {}", variables);
    }

    return builder.toString();
  }

  private Replacement searchParameterName(String content, int fromPosition, Set<String> names) {
    StringBuilder variableName = new StringBuilder();
    for (int i = fromPosition; i < content.length(); i++) {
      if(content.charAt(i) != '>') {
        variableName.append(content.charAt(i));
        continue;
      }
      String name = variableName.toString();
      if (names.contains(name)) {
        return new VariableReplacement(name);
      } else if (!name.contains(" ")) {
        return new UnknownReplacement(name);
      } else {
        return new NoReplacement();
      }
    }
    return new NoReplacement();
  }

  private interface Replacement {

    String name();

    default boolean needReplacement() {
      return false;
    }

    default int advancePositions() {
      return 0;
    }
  }

  private record VariableReplacement(String name) implements Replacement {

    @Override
    public boolean needReplacement() {
      return true;
    }

    @Override
    public int advancePositions() {
      return name.length() + 1; // variableName + '>'
    }
  }

  private record UnknownReplacement(String name) implements Replacement {

  }

  private record NoReplacement() implements Replacement {

    @Override
    public String name() {
      throw new NotImplementedException("This method should not be called");
    }
  }

}
