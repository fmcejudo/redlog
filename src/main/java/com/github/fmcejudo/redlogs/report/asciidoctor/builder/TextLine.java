package com.github.fmcejudo.redlogs.report.asciidoctor.builder;

@FunctionalInterface
public interface TextLine extends AsciiComponent {

    public static TextLine withText(String text) {
        return () -> text+"\n";
    }
}
