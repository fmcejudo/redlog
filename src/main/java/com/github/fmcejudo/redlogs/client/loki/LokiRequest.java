package com.github.fmcejudo.redlogs.client.loki;

import java.time.LocalDate;

public record LokiRequest(String query, LocalDate reportDate) {

}
