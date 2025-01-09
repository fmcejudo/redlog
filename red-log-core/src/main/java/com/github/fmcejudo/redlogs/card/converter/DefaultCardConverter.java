package com.github.fmcejudo.redlogs.card.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;

final class DefaultCardConverter implements CardConverter {

  private final Map<String, CardQueryConverter> cardQueryConverterMap;

  private final CardMetadataParser cardMetadataParser;

  DefaultCardConverter() {
    this.cardQueryConverterMap = new HashMap<>();
    this.cardMetadataParser = new CardMetadataParser();
  }

  public Iterator<CardQueryRequest> convert(final CardContext cardContext, final CardFile cardFile) {
    CardMetadata cardMetadata = cardMetadataParser.parse(cardContext, cardFile);
    try {
      return cardFile.queries().stream().map(cq -> cardQueryConverterMap.get(cq.processor()).convert(cq,cardMetadata)).iterator();
    } catch (Exception e) {
      throw new CardExecutionException(e.getMessage());
    }
  }

  @Override
  public boolean register(String key, CardQueryConverter cardQueryConverter) {
    if (cardQueryConverterMap.containsKey(key)) {
      return false;
    }

    cardQueryConverterMap.putIfAbsent(key, cardQueryConverter);
    return true;
  }

  @Override
  public boolean deregister(String key) {
    if (!cardQueryConverterMap.containsKey(key)) {
      return false;
    }
    cardQueryConverterMap.remove(key);
    return true;
  }

}

