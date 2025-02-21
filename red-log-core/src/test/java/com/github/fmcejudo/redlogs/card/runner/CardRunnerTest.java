package com.github.fmcejudo.redlogs.card.runner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.card.loader.CardFile;
import com.github.fmcejudo.redlogs.card.loader.CardFileLoader;
import io.github.fmcejudo.redlogs.card.AbstractCardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardMetadata;
import io.github.fmcejudo.redlogs.card.CardQuery;
import io.github.fmcejudo.redlogs.card.CardQueryRequest;
import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.validator.CardQueryValidator;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CardRunnerTest {

  @Test
  void shouldRunACard() {
    //Given
    CardFileLoader cardFileLoader = new TestCardFileLoader();
    CardConverter cardConverter = new TestCardConverter();
    Function<CardQueryRequest, CardQueryResponse> cardProcessor = new TestCardProcessor();
    CardExecutionWriter executionWriter = new TestExecutionWriter();
    TestReportWriter testReportWriter = new TestReportWriter();

    CardContext cardContext = CardContext.from("TEST", Map.of());

    //When
    String executionId = CardRunner.load(cardFileLoader).transform(cardConverter)
        .process(cardProcessor)
        .run(testReportWriter, executionWriter)
        .onCardContext(cardContext);

    //Then
    Assertions.assertThat(testReportWriter.getSaved()).isEqualTo(3);
    Assertions.assertThat(testReportWriter.getError()).isEqualTo(0);
    Assertions.assertThat(executionId).isNotNull().isNotEmpty();
  }

}

class TestCardFileLoader implements CardFileLoader {

  public CardFile load(CardContext cardExecutionContext) {
    return new CardFile(List.of(), LocalTime.now(), "2h", List.of(
        new CardQuery("id-one", "processor", "description", Map.of()),
        new CardQuery("id-two", "processor", "description", Map.of()),
        new CardQuery("id-three", "processor", "description", Map.of())
    ));
  }
}

class TestCardConverter implements CardConverter {

  @Override
  public Iterator<CardQueryRequest> convert(CardContext cardContext, CardFile cardFile) {
    LocalDateTime endTime = LocalDateTime.of(cardContext.reportDate(), LocalTime.now());
    CardMetadata cardMetadata = new CardMetadata("20", cardContext.applicationName(), endTime.minusMinutes(20), endTime);
    return cardFile.queries().stream().map(cq -> (CardQueryRequest) new TestCardQueryRequest(cq, cardMetadata)).iterator();
  }
}

class TestCardQueryRequest extends AbstractCardQueryRequest implements CardQueryRequest {

  TestCardQueryRequest(final CardQuery cardQuery, CardMetadata cardMetadata) {
    super(cardQuery, cardMetadata);
  }

  @Override
  public String processor() {
    return "test";
  }

  @Override
  public CardQueryValidator cardQueryValidator() {
    return cardQueryRequest -> {};
  }
}


class TestCardProcessor implements Function<CardQueryRequest, CardQueryResponse> {

  @Override
  public CardQueryResponse apply(CardQueryRequest cardQueryRequest) {
    return new CardQueryResponse(
        LocalDate.now(), cardQueryRequest.id(), cardQueryRequest.executionId(),
        cardQueryRequest.description(), cardQueryRequest.tags(), List.of(), "", null
    );
  }
}

class TestExecutionWriter implements CardExecutionWriter {

  @Override
  public String writeCardExecution(CardMetadata cardMetadata, Map<String, String> parameters) {
    System.out.println("write execution with id " + cardMetadata.executionId());
    return cardMetadata.executionId();
  }
}

class TestReportWriter implements CardReportWriter {

  private int saved = 0;
  private int error = 0;

  @Override
  public void onNext(CardQueryResponse cardTaskResult) {
      saved++;
  }

  @Override
  public void onError(Throwable throwable) {
    error++;
  }

  @Override
  public void onComplete() {
    System.out.println("complete test report");
  }

  public int getSaved() {
    return saved;
  }

  public int getError() {
    return error;
  }
}