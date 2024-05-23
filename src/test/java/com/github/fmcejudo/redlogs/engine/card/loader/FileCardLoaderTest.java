package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import com.github.fmcejudo.redlogs.engine.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.engine.card.converter.CardConverterConfiguration;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
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

        //When
        List<CardQueryRequest> cardQueryRequest = cardLoader.load("VALID_CARD", LocalDate.now());

        //Then
        Assertions.assertThat(cardQueryRequest).hasSize(2);
        Assertions.assertThat(cardQueryRequest).filteredOn(cq -> cq.id().equals("coffee")).first().satisfies(cqr -> {
            Assertions.assertThat(cqr.cardType()).isEqualTo(CardType.COUNT);
            Assertions.assertThat(cqr.query())
                    .contains("{app=\"redlog-sample\"}").contains("|~ `likes coffee`");
        });

        Assertions.assertThat(cardQueryRequest).filteredOn(cq -> cq.id().equals("chocolate")).first().satisfies(cqr -> {
            Assertions.assertThat(cqr.cardType()).isEqualTo(CardType.SUMMARY);
            Assertions.assertThat(cqr.query())
                    .contains("{app=\"redlog-sample\"}")
                    .contains("|~ `likes chocolate`");
        });
    }

}