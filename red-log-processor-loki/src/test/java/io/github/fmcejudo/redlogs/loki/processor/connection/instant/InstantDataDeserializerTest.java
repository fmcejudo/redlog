package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstantDataDeserializerTest {

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Data.class, new DataDeserializer());
        mapper.registerModules(module);
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

        QueryInstantResponse response = mapper.readValue(result, QueryInstantResponse.class);

        //Then
        Assertions.assertThat(response).isNotNull().satisfies(r -> {
            Assertions.assertThat(r.isSuccess()).isTrue();
            Assertions.assertThat(r.data().resultType()).isEqualTo("vector");
        });
    }

    @Test
    void shouldDeserializeAStreamResult() throws Exception {
        //Given
        String json = """
                 {
                  "status": "success",
                  "data": {
                    "resultType": "streams",
                    "result": [{
                        "stream": {
                          "level": "warn"
                        },
                        "values": [
                          "1588889221000000",
                          "1267.1266666666666"
                        ]
                      }]
                  }
                }
                """;

        //When
        QueryInstantResponse response = mapper.readValue(json, QueryInstantResponse.class);


        //Then
        Assertions.assertThat(response.isSuccess()).isTrue();
        Assertions.assertThat(response.data().resultType()).isEqualTo("streams");
        Assertions.assertThat(response.data().result()).hasSize(1).first().satisfies(result -> {
            Assertions.assertThat(result.streamsResult().stream()).containsEntry("level", "warn");
            Assertions.assertThat(result.streamsResult().values()).hasSize(1).first().satisfies(sv -> {
                Assertions.assertThat(sv.nanoSeconds()).isEqualTo("1588889221000000");
                Assertions.assertThat(sv.value()).isEqualTo("1267.1266666666666");
            });
        });

    }

}