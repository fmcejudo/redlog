package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DataDeserializer.class)
record Data(String resultType, List<Result> result) {

}
