package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record Item(String content) {

    public static Item fromMap(final Map<String, String> attributes, final String... lastLines) {
        StringBuilder builder = new StringBuilder();
        boolean hasLastLines = Optional.ofNullable(lastLines).map(l -> l.length > 0).orElse(false);

        Iterator<String> iterator = attributes.keySet().stream().sorted().toList().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.append("*").append(key).append("*: ").append(attributes.get(key));
            if (iterator.hasNext() || hasLastLines) {
                builder.append(" +");
            }
            builder.append("\n");
        }
        if (hasLastLines) {
            Stream.of(lastLines).forEach(line -> builder.append(line).append("\n"));
        }
        return new Item(builder.toString());
    }

    public static Item fromEnrichMap(final Map<String, AsciiComponent> attributes, final String... lastLines) {
        StringBuilder builder = new StringBuilder();

        Iterator<String> iterator = attributes.keySet().stream().sorted().toList().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String stringComponent = attributes.get(key).content();
            builder.append("*").append(key).append("*: ").append(stringComponent, 0, stringComponent.length()-1);
            if (iterator.hasNext()) {
                builder.append(" +");
            }
            builder.append("\n");
        }
        if (lastLines != null) {
            Stream.of(lastLines).forEach(line -> {
                builder.append(line).append("\n");
            });
        }
        return new Item(builder.toString());
    }
}
