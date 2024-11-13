package com.github.fmcejudo.redlogs.card.loader;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
record CardFile(@JsonProperty("common_query") String commonQuery,
                List<String> parameters,
                LocalTime time,
                String range,
                List<CardQuery> queries) {

}
