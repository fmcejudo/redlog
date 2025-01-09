package com.github.fmcejudo.redlogs.card.loader;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.ReplacementException;
import io.github.fmcejudo.redlogs.card.CardQuery;

abstract class AbstractCardFileLoader implements CardFileLoader {

  private final ObjectMapper mapper;

  private final ParameterReplacement parameterReplacement;

  AbstractCardFileLoader() {
    SimpleModule cardQueryModule = new SimpleModule();
    cardQueryModule.addDeserializer(CardQuery.class, new CardQueryDeserializer());
    this.mapper = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule(), cardQueryModule);
    this.parameterReplacement = new ParameterReplacement();
  }

  CardFile load(String content, CardContext cardContext) throws JsonProcessingException {
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
