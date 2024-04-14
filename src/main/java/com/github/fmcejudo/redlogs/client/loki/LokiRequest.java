package com.github.fmcejudo.redlogs.client.loki;

public record LokiRequest(RequestType requestType, String query) {

    public enum  RequestType {
        INSTANT, RANGE, POINT_IN_TIME
    }
}
