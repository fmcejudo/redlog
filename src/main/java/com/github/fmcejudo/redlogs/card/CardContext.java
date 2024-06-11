package com.github.fmcejudo.redlogs.card;

import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class CardContext {

    private final String applicationName;

    private final LocalDate reportDate;

    private final Map<String, String> parameters;

    private CardContext(String applicationName, LocalDate reportDate, Map<String, String> parameters) {
        this.applicationName = applicationName;
        this.reportDate = reportDate;
        this.parameters = parameters;
    }

    public static CardContext from(String applicationName, Map<String, String> parameters) {
        var reportDate = parseToDate(parameters.getOrDefault("date", LocalDate.now().format(ISO_LOCAL_DATE)));
        var usableParameters = prepareParameters(parameters);
        return new CardContext(applicationName, reportDate, usableParameters);
    }

    private static LocalDate parseToDate(String date) {
        LocalDate reportDate = LocalDate.now();
        if (Strings.isNotBlank(date)) {
            reportDate = LocalDate.parse(date, ISO_DATE);
        }
        return reportDate;
    }

    private static Map<String, String> prepareParameters(final Map<String, String> parameters) {
        if (parameters == null) {
            return Map.of();
        }
        HashMap<String, String> usableParams = new HashMap<>(parameters);
        usableParams.remove("date");
        return usableParams;
    }

    public String applicationName() {
        return applicationName;
    }

    public LocalDate reportDate() {
        return reportDate;
    }

    public Map<String, String> parameters() {
        return parameters;
    }
}
