package io.github.fmcejudo.redlogs.card.domain;

import java.util.Map;

public record CardQueryResponseEntry(Map<String, String> labels, long count) {
}
