package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;

@FunctionalInterface
public interface DocumentTitle extends AsciiComponent {

    public static DocumentTitle withText(String title) {
        return () -> {
            return "== " + title;
        };
    }

    default DocumentTitle setLink(String link, String description) {
        return () -> {
            String title = this.content();
            return title + " +\nlink:" + link + "[" + description + "]";
        };
    }
}
