package io.github.fmcejudo.redlogs.loki.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse;
import io.github.fmcejudo.redlogs.loki.processor.connection.LokiResponse.LokiResult;

@FunctionalInterface
interface LokiResponseGenerator {

  LokiResponse generate();

  static LokiResponseGenerator status(boolean status) {
    return () -> {
      TestLokiResponse lokiResponse = new TestLokiResponse();
      lokiResponse.setStatus(status);
      return lokiResponse;
    };
  }

  default LokiResponseGenerator addResult(Map<String, String> labels, long count) {
    return () -> {
      LokiResult lokiResult = new LokiResult(labels, count);
      TestLokiResponse generate = (TestLokiResponse) this.generate();
      generate.addResult(lokiResult);
      return generate;
    };
  }

}

class TestLokiResponse implements LokiResponse {

  public final List<LokiResult> resultList;

  public boolean status;

  TestLokiResponse() {
    this.resultList = new ArrayList<>();
  }

  @Override
  public boolean isSuccess() {
    return status;
  }

  @Override
  public List<LokiResult> result() {
    return Collections.unmodifiableList(resultList);
  }

  public TestLokiResponse addResult(LokiResult lokiResult) {
    resultList.add(lokiResult);
    return this;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }
}

