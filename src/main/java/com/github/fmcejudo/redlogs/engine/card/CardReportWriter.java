package com.github.fmcejudo.redlogs.engine.card;

class CardReportWriter {

    public void printReport(CardTaskResult cardTaskResult) {
        if (cardTaskResult.isValid()) {
            CardReportEntries success = cardTaskResult.success();
            StringBuilder builder = new StringBuilder("\n\n* **")
                    .append(success.description()).append("**").append(": ")
                    .append(success.cardReportEntries().size()).append(" elements\n\n");
            success.cardReportEntries().forEach(cre -> {
                builder.append("""
                        - labels: %s
                          count: %d
                        """.formatted(cre.labels(), cre.count()));
            });
            System.out.println(builder);
        }
    }
}
