package com.github.fmcejudo.redlogs.execution.domain;


import java.time.LocalDate;
import java.util.Map;

public record Execution(String id, String application, Map<String, String> parameters, LocalDate reportDate) {

}
