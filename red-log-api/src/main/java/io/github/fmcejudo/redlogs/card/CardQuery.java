package io.github.fmcejudo.redlogs.card;

import java.util.List;
import java.util.Map;

public record CardQuery(String id, String processor, String description, List<String> tags, Map<String, String> properties) {

}
