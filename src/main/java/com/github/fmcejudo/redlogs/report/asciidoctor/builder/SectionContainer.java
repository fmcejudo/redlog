package com.github.fmcejudo.redlogs.report.asciidoctor.builder;

@FunctionalInterface
public interface SectionContainer extends AsciiComponent {

    public static SectionContainer startWithComponent(AsciiComponent asciiComponent) {

        return () -> "\n"+asciiComponent.content() + "\n";
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
