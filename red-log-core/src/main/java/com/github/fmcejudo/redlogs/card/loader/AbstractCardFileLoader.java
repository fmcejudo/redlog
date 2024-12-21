package com.github.fmcejudo.redlogs.card.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.fmcejudo.redlogs.card.CardQuery;

abstract class AbstractCardFileLoader implements CardFileLoader {

  private final ObjectMapper mapper;

  AbstractCardFileLoader() {
    SimpleModule cardQueryModule = new SimpleModule();
    cardQueryModule.addDeserializer(CardQuery.class, new CardQueryDeserializer());
    this.mapper = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule(), cardQueryModule);
  }

  CardFile load(String content) throws JsonProcessingException {

    //TODO: In this load, it needs to replace parameters, to have ready the information to deal with
    return mapper.readValue(content, CardFile.class);
  }

}
