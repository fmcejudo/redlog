package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.converter.CardConverterConfiguration;
import com.github.fmcejudo.redlogs.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.CardType;
import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CardConverterConfiguration.class,
        CardConfiguration.class
})
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackageClasses = {RedLogFileProperties.class})
@TestPropertySource(properties = {
        "redlog.file.files-path=classpath:cards/",
        "redlog.source-type=FILE"
})
class FileCardLoaderTest {

    @Autowired
    CardLoader cardLoader;


    @Test
    void shouldLoadAValidCard() {
        //Given
        String applicationName = "VALID_CARD";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of(
                        "date", LocalDate.now().format(ISO_LOCAL_DATE),
                        "environment", "local",
                        "host", "localhost"
                ));

        //When
        List<CardQueryRequest> cardQueryRequest = cardLoader.load(cardExecutionContext);

        //Then
        Assertions.assertThat(cardQueryRequest).hasSize(2);
        Assertions.assertThat(cardQueryRequest).filteredOn(cq -> cq.id().equals("coffee")).first().satisfies(cqr -> {
            Assertions.assertThat(cqr.cardType()).isEqualTo(CardType.COUNT);
            Assertions.assertThat(cqr.executionId()).isNull();
            Assertions.assertThat(cqr.query())
                    .contains("{app=\"redlog-sample\", environment=\"local\", host=\"localhost\"}")
                    .contains("|~ `likes coffee`");
        });

        Assertions.assertThat(cardQueryRequest).filteredOn(cq -> cq.id().equals("chocolate")).first().satisfies(cqr -> {
            Assertions.assertThat(cqr.cardType()).isEqualTo(CardType.SUMMARY);
            Assertions.assertThat(cqr.query())
                    .contains("{app=\"redlog-sample\", environment=\"local\", host=\"localhost\"}")
                    .contains("|~ `likes chocolate`");
        });
    }


    @Test
    void shouldFailOnUnknownParameters() {
        //Given
        String applicationName = "VALID_CARD";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of(
                        "date", LocalDate.now().format(ISO_LOCAL_DATE)
                ));

        //When && Then
        Assertions.assertThatThrownBy(() -> cardLoader.load(cardExecutionContext))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("parameters '[environment, host]' not found in parameter map");

    }

}