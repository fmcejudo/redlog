package com.github.fmcejudo.redlogs.card.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator.CardQueryValidation;

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
      if (cardQueryConverterMap.isEmpty()) {
        throw new CardExecutionException("There are no processors registered yet");
      }
      List<CardQuery> cardQueries = cardFile.queries();
      checkAndFailOnUnprocessableCardQueries(cardQueries);
      return convertAndValidateCardQueries(cardQueries, cardMetadata);
    } catch (Exception e) {
      throw new CardExecutionException(e.getMessage());
    }
  }

  private Iterator<CardQueryRequest> convertAndValidateCardQueries(final List<CardQuery> cardQueries, final CardMetadata cardMetadata) {
    List<CardQueryRequest> successConvertedCards = new ArrayList<>();
    List<CardQueryRequest> failedConvertedCards = new ArrayList<>();
    for (CardQuery cardQuery : cardQueries) {
      CardQueryConverter cardQueryConverter = cardQueryConverterMap.get(cardQuery.processor());
      CardQueryRequest cardQueryRequest = cardQueryConverter.convert(cardQuery, cardMetadata);
      CardQueryValidation cardQueryValidation = cardQueryConverter.validator().validate(cardQueryRequest);
      if (cardQueryValidation.isSuccess()) {
        successConvertedCards.add(cardQueryRequest);
      } else {
        failedConvertedCards.add(cardQueryRequest);
      }
    }

    if (!failedConvertedCards.isEmpty()) {
      throw new CardExecutionException("validation for card requests have failed");
    }

    return List.copyOf(successConvertedCards).iterator();
  }

  private void checkAndFailOnUnprocessableCardQueries(List<CardQuery> cardQueries) {
    List<CardQuery> unprocessableCardQueries = findUnprocessableCardQueries(cardQueries);
    if (unprocessableCardQueries.isEmpty()) {
      return;
    }
    String partialErrorMessage = unprocessableCardQueries.stream()
        .map(cq -> "(id -> '%s', processor -> '%s')".formatted(cq.id(), cq.processor()))
        .collect(Collectors.joining(", "));
    throw new CardExecutionException("There are card queries with unknown processors: " + partialErrorMessage);
  }

  private List<CardQuery> findUnprocessableCardQueries(final List<CardQuery> cardQueries) {
    Set<String> knownProcessorKeys = cardQueryConverterMap.keySet();
    return cardQueries.stream().filter(cq -> !knownProcessorKeys.contains(cq.processor())).toList();
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

