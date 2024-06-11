package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FunctionalInterface
public interface DocumentTitle extends AsciiComponent {

    public static DocumentTitle level(int level) {
        return () -> {
            return IntStream.range(0, level).mapToObj(i -> "=").collect(Collectors.joining());
        };
    }

    default DocumentTitle withText(String title) {
        return () -> this.documentTitleContent() + ' ' + title;
    }

    default DocumentTitle setLink(String link, String description) {
        return () -> {
            String title = this.documentTitleContent();
            return title + "\nlink:" + link + "[" + description + "]\n";
        };
    }

    String documentTitleContent();

    default String content(){
        return "\n" + documentTitleContent() + "\n";
    }
}
