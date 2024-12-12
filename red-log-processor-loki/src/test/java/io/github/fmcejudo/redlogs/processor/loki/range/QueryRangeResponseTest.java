package io.github.fmcejudo.redlogs.processor.loki.range;

import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.processor.loki.LokiResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryRangeResponseTest {

    @Test
    void shouldRecogniseASuccessResponse() {
        //Given
        QueryRangeResponse response = new QueryRangeResponse("success", null);

        //When && Then
        Assertions.assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldParseDataStream() {
        //Given
        Data data = DataGenerator.resultType(DataGenerator.DataType.STREAMS)
                .addStreamsResult(Map.of("streams","value"), List.of(new StreamsValue("20","2")))
                .generate();

        QueryRangeResponse response = new QueryRangeResponse("success", data);

        //When
        List<LokiResponse.LokiResult> result = response.result();

        //Then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(response.result().stream().toList()).allSatisfy(lokiResult ->
                Assertions.assertThat(lokiResult.labels()).containsKey("time")
        );
    }

    @Test
    void shouldParseADataMatrix() {
        //Given
        Data data = DataGenerator.resultType(DataGenerator.DataType.MATRIX)
                .addMatrixResult(Map.of("key", "value"), List.of(new MatrixValue(20, "4")))
                .generate();

        QueryRangeResponse response = new QueryRangeResponse("success", data);

        //When
        List<LokiResponse.LokiResult> result = response.result();

        //Then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(response.result().stream().toList()).allSatisfy(lokiResult -> {
            Assertions.assertThat(lokiResult.labels()).containsEntry("key", "value");
        });
    }

}