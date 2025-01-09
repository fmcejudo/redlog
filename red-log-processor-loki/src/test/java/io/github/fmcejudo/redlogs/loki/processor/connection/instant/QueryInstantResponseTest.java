package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryInstantResponseTest {


    @Test
    void shouldParseAStreamsResult() {
        //Given
        Data data = DataGenerator.withResultType(DataGenerator.ResultType.STREAM)
                .addStreamsResult(Map.of("key", "value"), List.of(new StreamsValue("20", "2")))
                .generate();

        //When
        QueryInstantResponse queryInstantResponse = new QueryInstantResponse("success", data);

        //Then
        Assertions.assertThat(queryInstantResponse.isSuccess()).isTrue();
        Assertions.assertThat(queryInstantResponse.result()).hasSize(1).first().satisfies(lr -> {
            Assertions.assertThat(lr.labels()).containsEntry("key", "value");
        });
    }

    @Test
    void shouldParseAVectorResult() {
        //Given
        Data data = DataGenerator.withResultType(DataGenerator.ResultType.VECTOR)
                .addVectorResult(Map.of("key", "value"), List.of(new VectorValue(20, "2")))
                .generate();

        //When
        QueryInstantResponse queryInstantResponse = new QueryInstantResponse("success", data);

        //Then
        Assertions.assertThat(queryInstantResponse.isSuccess()).isTrue();
        Assertions.assertThat(queryInstantResponse.result()).hasSize(1).first().satisfies(lr -> {
            Assertions.assertThat(lr.labels()).containsEntry("key", "value");
        });
    }

}