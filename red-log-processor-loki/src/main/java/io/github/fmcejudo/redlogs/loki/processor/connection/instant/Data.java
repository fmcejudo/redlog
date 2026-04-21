package io.github.fmcejudo.redlogs.loki.processor.connection.instant;

import java.util.List;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DataDeserializer.class)
public record Data(String resultType, List<Result> result) {

}
