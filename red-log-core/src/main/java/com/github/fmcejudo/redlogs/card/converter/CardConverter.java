package com.github.fmcejudo.redlogs.card.converter;

import java.util.Iterator;
import java.util.function.BiFunction;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import org.apache.commons.lang3.NotImplementedException;

@FunctionalInterface
public interface CardConverter extends BiFunction<CardContext, CardFile, Iterator<CardQueryRequest>> {

  @Override
  default Iterator<CardQueryRequest> apply(CardContext cardContext, CardFile cardFile) {
    return this.convert(cardContext, cardFile);
  }

  Iterator<CardQueryRequest> convert(final CardContext cardContext, final CardFile cardFile);

  default boolean register(String key, CardQueryConverter cardQueryConverter) {
    throw new NotImplementedException("register method is not implemented in your card converter");
  }

  default boolean deregister(String key) {
    throw new NotImplementedException("deregister method is not implemented in your card converter");
  }
}
