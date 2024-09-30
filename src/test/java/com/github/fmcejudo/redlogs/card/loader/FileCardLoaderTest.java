package com.github.fmcejudo.redlogs.card.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.converter.CardConverterConfiguration;
import com.github.fmcejudo.redlogs.card.model.CardRequest;
import com.github.fmcejudo.redlogs.card.model.CounterCardQueryRequest;
import com.github.fmcejudo.redlogs.card.model.SummaryCardQueryRequest;
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
        CardRequest cardRequest = cardLoader.load(cardExecutionContext);

        //Then
        Assertions.assertThat(cardRequest.cardQueryRequests()).hasSize(2);
        Assertions.assertThat(cardRequest.cardQueryRequests()).filteredOn(cq -> cq.id().equals("coffee")).first()
                .satisfies(cqr -> {
                    Assertions.assertThat(cqr).isInstanceOf(CounterCardQueryRequest.class);
                    Assertions.assertThat(cqr.executionId()).isNull();
                    Assertions.assertThat(cqr.query())
                            .contains("{app=\"redlog-sample\", environment=\"local\", host=\"localhost\"}")
                            .contains("|~ `likes coffee`");
                });

        Assertions.assertThat(cardRequest.cardQueryRequests()).filteredOn(cq -> cq.id().equals("chocolate")).first()
                .satisfies(cqr -> {
                    Assertions.assertThat(cqr).isInstanceOf(SummaryCardQueryRequest.class);
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