package com.github.fmcejudo.redlogs.card.report.asciidoctor.builder;

import java.util.Iterator;
import java.util.List;

public interface ListItem extends AsciiComponent {

    public static ListItem createList(List<Item> items) {
        return () -> {
            StringBuilder builder = new StringBuilder();
            Iterator<Item> iterator = items.iterator();
            while (iterator.hasNext()) {
                Item next = iterator.next();
                builder.append(formatContent(next.content()));
                if (iterator.hasNext()) {
                    builder.append("\n");
                }
            }
            return builder.toString();
        };
    }

    public default ListItem addItem(final Item item) {
        return () -> {
            String previous = this.content();
            String formatContent = formatContent(item.content());
            return previous+"\n"+formatContent;
        };
    }

    private static String formatContent(final String content) {
        String indentContent = content.indent(2);
        return "*" + indentContent.substring(1);
    }
}
