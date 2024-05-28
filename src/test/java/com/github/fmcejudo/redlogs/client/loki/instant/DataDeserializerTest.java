package com.github.fmcejudo.redlogs.client.loki.instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DataDeserializerTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldDeserializeAVectorResult() throws IOException {
        //Given
        String result = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "vector",
                    "result": [{
                        "metric": {},
                        "value": [
                          1588889221,
                          "1267.1266666666666"
                        ]
                      },{
                        "metric": {
                          "level": "warn"
                        },
                        "value": [
                          1588889221,
                          "37.77166666666667"
                        ]
                      }]
                  }
                }
                """;

        //When
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Data.class, new DataDeserializer());
        mapper.registerModules(module);

        QueryInstantResponse response = mapper.readValue(result, QueryInstantResponse.class);

        //Then
        Assertions.assertThat(response).isNotNull().satisfies(r -> {
            Assertions.assertThat(r.isSuccess()).isTrue();
            Assertions.assertThat(r.data().resultType()).isEqualTo("vector");
        });
    }

    @Test
    void shouldDeserializeAStreamResult() {
        //Given

        //When

        //Then
    }

}