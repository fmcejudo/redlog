package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardType;
import org.springframework.stereotype.Component;

import java.util.List;

public enum CardEnum {

    /*
    ALERTHUB(new Card("ALERTHUB", "des", List.of(
            new CardQueryRequest(
                    "Response with 4.x.x code",
                    "Response with 4.x.x code",
                    CardType.SERVICE,
                    """
                        %s
                        |~ `422 Unprocessable Entity`
                        | json | line_format "- {{.level}} - {{.short_message}}"
                    """.formatted(CommonQueries.QUERY),
                    "alert"
            ), new CardQueryRequest(
                    "Response with 4.x.x code",
                    "Response with 4.x.x code",
                    CardType.SERVICE,
                    """
                            sum by (short_message)(count_over_time(
                               %s
                               |~ `422 Unprocessable Entity` | json | line_format `- {{.level}} - {{.short_message}}`[1m]
                            ))
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),
            new CardQueryRequest(
                    "Gateway timeout",
                    "Gateway timeout",
                    CardType.SERVICE,
                    """
                            sum by(platform,short_message) (count_over_time(
                                %s
                                |~ `504 Gateway Timeout from POST`
                                | json | line_format `- {{.level}} - {{.short_message}}`[24h]
                            ))
                            """.formatted(CommonQueries.QUERY),
                    "alert"
            ),
            new CardQueryRequest(
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
            new CardQueryRequest(
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
            new CardQueryRequest(
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
            new CardQueryRequest(
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
            new CardQueryRequest(
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
            new CardQueryRequest("tags-matching", "tags matching queries", CardType.COUNT, "{app=\"ALERTAPI\"}", "definition")
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

    @Override
    public List<CardQueryRequest> load(String application) {
        return CardEnum.valueOf(application).getCard().cardQueries();
    }

    private static class CommonQueries {
        public static final String QUERY = """
                {stream_filter="ALERTHUB", level="ERROR", environment="pre", slot="engine"} \
                """;
    }
     */
}
