package io.github.fmcejudo.redlogs.card;

import java.util.Map;

import io.github.fmcejudo.redlogs.card.converter.CardQueryConverter;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;

public interface RedlogPluginProvider {

  CardQueryProcessor createProcessor(Map<String, String> connectionDetails);

  CardQueryConverter createCardQueryConverter();

  String type();

}
