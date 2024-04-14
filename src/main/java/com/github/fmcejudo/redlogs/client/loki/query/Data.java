package com.github.fmcejudo.redlogs.client.loki.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = DataDeserializer.class)
record Data(String resultType, List<Result> result) {
}
