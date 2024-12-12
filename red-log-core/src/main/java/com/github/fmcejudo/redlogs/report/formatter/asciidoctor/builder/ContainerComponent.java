package com.github.fmcejudo.redlogs.report.formatter.asciidoctor.builder;

@FunctionalInterface
public interface ContainerComponent extends AsciiComponent {

    public static ContainerComponent create() {
        return () -> {
            return "";
        };
    }

    default ContainerComponent addComponent(AsciiComponent asciiComponent) {
        return () -> {
            String content = this.content();
            return content + asciiComponent.content();
        };
    }

}
