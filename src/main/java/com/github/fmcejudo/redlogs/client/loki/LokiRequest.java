package com.github.fmcejudo.redlogs.client.loki;

import java.time.LocalDate;

public record LokiRequest(RequestType requestType, String query, LocalDate reportDate) {

    public enum  RequestType {
        INSTANT, RANGE, POINT_IN_TIME
    }
}
