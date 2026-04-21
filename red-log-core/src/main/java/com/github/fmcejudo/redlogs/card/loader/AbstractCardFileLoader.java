package com.github.fmcejudo.redlogs.card.loader;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.ReplacementException;
import io.github.fmcejudo.redlogs.card.CardQuery;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLMapper;

abstract class AbstractCardFileLoader implements CardFileLoader {

  private final ObjectMapper mapper;

  private final ParameterReplacement parameterReplacement;

  AbstractCardFileLoader() {
    SimpleModule cardQueryModule = new SimpleModule();
    cardQueryModule.addDeserializer(CardQuery.class, new CardQueryDeserializer());
    this.mapper = YAMLMapper.builder()
        .addModule(cardQueryModule)
        .build();
    this.parameterReplacement = new ParameterReplacement();
  }

  CardFile load(String content, CardContext cardContext) {
    String contentWithReplacedParams = parameterReplacement.replace(content, cardContext.parameters());
    CardFile cardFile = mapper.readValue(contentWithReplacedParams, CardFile.class);
    checkParameterReplacement(cardFile.parameters(), cardContext.parameters().keySet());
    return cardFile;
  }

  private void checkParameterReplacement(final Collection<String> requiredParameters, final Collection<String> providedParameters) {

    if (providedParameters.containsAll(requiredParameters)) {
      return;
    }
    List<String> unknownVariables = requiredParameters.stream().filter(Predicate.not(providedParameters::contains)).toList();
    throw new ReplacementException(unknownVariables);
  }

}
