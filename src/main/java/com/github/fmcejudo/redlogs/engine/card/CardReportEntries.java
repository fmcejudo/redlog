package com.github.fmcejudo.redlogs.engine.card;

import java.util.List;
import java.util.Map;

record CardReportEntries(CardReportState cardReportState,
                         String id,
                         String description,
                         List<CardReportEntry> cardReportEntries,
                         List<CardException> exceptions) {

    static CardReportEntries failed(String id, String description, List<CardException> exceptions) {
        return new CardReportEntries(CardReportState.FAILED, id, description, List.of(), exceptions);
    }

    static CardReportEntries success(String id, String description, List<CardReportEntry> entries) {
        return new CardReportEntries(CardReportState.SUCCESS, id, description, entries, List.of());
    }

}

record CardReportEntry(String id, String description, Map<String, String> labels, long count) {
}

enum CardReportState {
    SUCCESS, FAILED
}
