package io.github.fmcejudo.redlogs;

import io.github.fmcejudo.redlogs.mongo.MongoNamingUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MongoNamingUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "collection name", "collection_name", "Collection_Name", "collection     Name", "COLLECTION NAME"
    })
    void shouldComposeName(final String collectionName) {

        //Given
        final String prefix = "prefix";

        //When
        String composedName = MongoNamingUtils.composeCollectionName(prefix, collectionName);

        //Then
        Assertions.assertThat(composedName).isEqualTo("prefixCollectionName");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @NullSource
    void shouldAcceptNullOrEmptyPrefix(final String prefix) {

        //Given
        final String collectionName = "collection_name";

        //When
        String composedName = MongoNamingUtils.composeCollectionName(prefix, collectionName);

        //Then
        Assertions.assertThat(composedName).isEqualTo("collectionName");
    }

}