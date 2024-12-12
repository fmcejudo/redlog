package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;

@FunctionalInterface
public interface SectionContainer extends AsciiComponent {

    public static SectionContainer startWithComponent(AsciiComponent asciiComponent) {
        return asciiComponent::content;
    }

    default SectionContainer add(AsciiComponent component) {
        return () -> {
            String content = this.content();
            return """
                    %s
                    %s""".formatted(content, component.content());
        };
    }

}
