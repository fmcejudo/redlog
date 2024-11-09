package io.github.fmcejudo.redlogs.processor.loki.range;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = DataDeserializer.class)
record Data(String resultType, List<Result> result) {
}
