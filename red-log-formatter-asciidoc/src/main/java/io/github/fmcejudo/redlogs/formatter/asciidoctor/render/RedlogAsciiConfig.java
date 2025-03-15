package io.github.fmcejudo.redlogs.formatter.asciidoctor.render;

import java.util.Map;

public record RedlogAsciiConfig(boolean hasContentTable,
                                boolean hasPagination,
                                Map<String, String> metadata) {

}
