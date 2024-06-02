package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;

public interface LinkComponent extends AsciiComponent {

    public static LinkComponent link(String link, String description) {
        return () -> "link:%s[%s]\n".formatted(link, description);
    }
}
