package com.github.fmcejudo.redlogs.card.loader;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.fmcejudo.redlogs.card.CardQuery;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record CardFile(List<String> parameters,
                LocalTime time,
                String range,
                List<CardQuery> queries) {

}

