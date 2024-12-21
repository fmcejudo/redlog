package io.github.fmcejudo.redlogs.card;

import java.util.Map;

public record CardQueryResponseEntry(Map<String, String> labels, long count) {
}
