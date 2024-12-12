package io.github.fmcejudo.redlogs.processor.loki.range;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RangeDataDeserializerTest {

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Data.class, new DataDeserializer());
        mapper.registerModules(module);
    }

    @Test
    void shouldDeserializeAMatrix() throws Exception {
        //Given
        String json = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "matrix",
                    "result": [
                      {
                       "metric": {
                          "level": "info"
                        },
                        "values": [
                          [
                            1588889221,
                            "137.95"
                          ]
                        ]
                      }
                    ]
                  }
                }
                """;

        //When
        QueryRangeResponse queryRangeResponse = mapper.readValue(json, QueryRangeResponse.class);

        //Then
        Assertions.assertThat(queryRangeResponse.isSuccess()).isTrue();

        Assertions.assertThat(queryRangeResponse.data().resultType()).isEqualTo("matrix");

        Assertions.assertThat(queryRangeResponse.data().result()).hasSize(1).allSatisfy(r -> {

            Assertions.assertThat(r.matrixResult().metric()).containsEntry("level", "info");
            Assertions.assertThat(r.matrixResult().value()).hasSize(1).first().satisfies(mv -> {
                Assertions.assertThat(mv.seconds()).isEqualTo(1588889221);
                Assertions.assertThat(mv.value()).isEqualTo("137.95");
            });

        });

    }

    @Test
    void shouldDeserializeAStream() throws Exception {
        //Given
        String json = """
                {
                   "status": "success",
                   "data": {
                     "resultType": "streams",
                     "result": [
                       {
                         "stream": {
                           "filename": "/var/log/myproject.log",
                           "job": "varlogs",
                           "level": "info"
                         },
                         "values": [
                           [
                             "1569266497240578000",
                             "foo"
                           ],
                           [
                             "1569266492548155000",
                             "bar"
                           ]
                         ]
                       }
                     ]
                   }
                 }
                """;

        //When
        QueryRangeResponse queryRangeResponse = mapper.readValue(json, QueryRangeResponse.class);

        //Then
        Assertions.assertThat(queryRangeResponse.isSuccess()).isTrue();

        Assertions.assertThat(queryRangeResponse.data().resultType()).isEqualTo("streams");

        Assertions.assertThat(queryRangeResponse.data().result()).hasSize(1).allSatisfy(r -> {

            Assertions.assertThat(r.streamsResult().stream()).containsEntry("level", "info");
            Assertions.assertThat(r.streamsResult().values()).hasSize(2).first().satisfies(sv -> {
                Assertions.assertThat(sv.nanoSeconds()).isEqualTo("1569266497240578000");
                Assertions.assertThat(sv.value()).isEqualTo("foo");
            });

        });


    }

}