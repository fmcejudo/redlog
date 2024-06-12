package com.github.fmcejudo.redlogs.card.converter;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

class CardConverterTest {

    private CardConverter cardConverter;

    LoadCardContent loadCardContent;

    @BeforeEach
    void setUp() {
        cardConverter = new DefaultCardConverter();
        loadCardContent = LoadCardContent.createInstance();
    }

    @Test
    void shouldParseCardFile() throws IOException {
        //Given
        final String applicationName = "CARD_SET_TIME_RANGE";
        CardContext cardContext = CardContext.from(applicationName, Map.of("range", "24h"));
        String cardContent = loadCardContent.apply(applicationName);

        //When
        List<CardQueryRequest> cardQueryRequests = cardConverter.convert(cardContent, cardContext);

        //Then
        Assertions.assertThat(cardQueryRequests).hasSize(1).first().satisfies(cardQueryRequest -> {
            Assertions.assertThat(cardQueryRequest.description()).isEqualTo("description");
            Assertions.assertThat(cardQueryRequest.id()).isEqualTo("something");
            Assertions.assertThat(cardQueryRequest.query()).isEqualTo("""
                    sum by(host)(
                    [24h])""");
        });
    }

    @FunctionalInterface
    private interface LoadCardContent extends Function<String, String> {

        public static  LoadCardContent createInstance() {
            ClassLoader classLoader = LoadCardContent.class.getClassLoader();
            return applicationName -> {
                InputStream is = classLoader.getResourceAsStream("cards/" + applicationName + ".yaml");
                try {
                    return IOUtils.toString(is, UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

}