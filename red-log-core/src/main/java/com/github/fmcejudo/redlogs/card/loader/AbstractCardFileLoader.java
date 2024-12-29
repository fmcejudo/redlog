package com.github.fmcejudo.redlogs.card.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fmcejudo.redlogs.card.CardContext;
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
    return mapper.readValue(contentWithReplacedParams, CardFile.class);
  }


}
