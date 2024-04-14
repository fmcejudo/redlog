package com.github.fmcejudo.redlogs.engine.card;

import java.util.List;

enum CardEnum {

    ALERTHUB(new Card("ALERTHUB", "des", List.of(
            /*new CardQuery(
                    "Response with 4.x.x code",
                    "Response with 4.x.x code",
                    CardType.SERVICE,
                    """
                        %s
                        |~ `422 Unprocessable Entity`
                        | json | line_format "- {{.level}} - {{.short_message}}"
                    """.formatted(CommonQueries.QUERY),
                    "alert"
            ),*/ new CardQuery(
                    "Response with 4.x.x code",
                    "Response with 4.x.x code",
                    CardType.SERVICE,
                    """
                            sum(count_over_time(
                               %s
                               |~ `422 Unprocessable Entity` | json | line_format `- {{.level}} - {{.short_message}}`[1m]
                            ))
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),
         /*   new CardQuery(
                    "Gateway timeout",
                    "Gateway timeout",
                    CardType.SERVICE,
                    """
                            sum by(platform) (count_over_time(
                                %s
                                |~ `504 Gateway Timeout from POST`
                                | json | line_format `- {{.level}} - {{.short_message}}`[24h]
                            ))
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),*/
            new CardQuery(
                    "Redis not connected",
                    "Redis not connected",
                    CardType.SERVICE,
                    """
                            sum by(platform) (count_over_time(
                                %s
                                |~ `RedisException: Currently not connected`
                                | json | line_format `- {{.level}} - {{.short_message}}`[1m]
                            ))
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),
            new CardQuery(
                    "Number Format Exception",
                    "Number Format Exception",
                    CardType.COUNT,
                    """
                            sum by(alert) (count_over_time(
                                %s
                                |~ `check queries java.lang.NumberFormatException: For input string:`
                                | json | line_format "- {{.level}} - {{.short_message}}" [24h]
                            ))
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),
            new CardQuery(
                    "Network issues",
                    "Network issues",
                    CardType.SERVICE,
                    """
                            sum by(timestamp) (
                               count_over_time(
                               %s
                               |~ `SslHandshakeTimeoutException: handshake timed out after 10000ms|Connection reset by peer|Connection reset by peer`
                               | json | line_format "- {{.level}} - {{.short_message}}" [1m]
                               )
                            )
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),
            new CardQuery(
                    "tags-matching",
                    "tags matching queries",
                    CardType.COUNT,
                    """
                            sum by(alert,short_message)(
                               count_over_time(
                                   %s
                                   |~ `TimeSeries Database did not return the same matching tags`
                                   | json | line_format "- {{.level}} - {{.short_message}}" [24h]
                                )
                            )""".formatted(CommonQueries.QUERY),
                    "def"
            ),
            new CardQuery(
                    "Alert Definitions in Quarantine",
                    "Alert Definitions in Quarantine",
                    CardType.COUNT,
                    """
                            sum by(alert,short_message)(
                               count_over_time(
                                   %s
                                   |~ `Task is quarantined and it will be skipped`
                                   | json | line_format "- {{.level}} - {{.short_message}}" [24h]
                                )
                            )""".formatted(CommonQueries.QUERY),
                    "def"
            )
    ))),

    ALERTAPI(new Card("ALERTAPI", "des", List.of(
            new CardQuery("tags-matching", "tags matching queries", CardType.COUNT, "{app=\"ALERTAPI\"}", "definition")
    )));

    private final Card card;

    CardEnum(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    static Card getCardByName(final String name) {
        return CardEnum.valueOf(name).getCard();
    }

    private static class CommonQueries {
        public static final String QUERY = """
                {stream_filter="ALERTHUB", level="ERROR", environment="pro", slot="engine"} \
                """;
    }
}
