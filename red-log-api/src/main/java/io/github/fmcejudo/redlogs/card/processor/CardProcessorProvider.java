package io.github.fmcejudo.redlogs.card.processor;

import java.util.Map;

public interface CardProcessorProvider {

  CardProcessor createProcessor(Map<String, String> connectionDetails);

}
