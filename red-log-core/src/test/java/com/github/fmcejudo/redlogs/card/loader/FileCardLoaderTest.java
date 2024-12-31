package com.github.fmcejudo.redlogs.card.loader;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.LocalDate;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.converter.CardConverterConfiguration;
import com.github.fmcejudo.redlogs.card.exception.ReplacementException;
import com.github.fmcejudo.redlogs.config.RedLogConfigProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CardConverterConfiguration.class,
        CardLoaderConfiguration.class
})
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackageClasses = {RedLogConfigProperties.class})
@TestPropertySource(properties = {
        "redlog.source.file.files-path=classpath:cards/",
        "redlog.source.type=FILE"
})
class FileCardLoaderTest {

    @Autowired
    CardFileLoader cardLoader;

    @Test
    void shouldLoadAValidCard() {
        //Given
        String applicationName = "VALID_CARD";
        var cardExecutionContext =
                CardContext.from(applicationName, Map.of(
                        "date", LocalDate.now().format(ISO_LOCAL_DATE),
                        "environment", "local",
                        "host", "localhost",
                        "range", "24h"
                ));

        //When
        CardFile cardFile = cardLoader.load(cardExecutionContext);

        //Then
        Assertions.assertThat(cardFile).isNotNull();

    }

    @Test
    void shouldFailOnUnreplacedParameters() {
        //Given
        String applicationName = "VALID_CARD";
        var cardExecutionContext =
            CardContext.from(applicationName, Map.of(
                "date", LocalDate.now().format(ISO_LOCAL_DATE),
                "host", "localhost"
            ));

        //When && Then
        Assertions.assertThatThrownBy(() -> cardLoader.load(cardExecutionContext)).isInstanceOf(ReplacementException.class)
            .hasMessageContaining("parameters 'environment' and 'range' have not been found");


    }

}