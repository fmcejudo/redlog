package com.github.fmcejudo.redlogs.engine.card.model;

import java.util.Map;

public record CardQueryResponseEntry(Map<String, String> labels, long count) {
}
