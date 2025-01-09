package io.github.fmcejudo.redlogs.card;

import java.util.Map;

public record CardQuery(String id, String processor, String description, Map<String, String> properties) {

}
