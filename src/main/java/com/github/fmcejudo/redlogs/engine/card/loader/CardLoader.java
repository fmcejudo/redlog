package com.github.fmcejudo.redlogs.engine.card.loader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.engine.card.model.CardType;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface CardLoader {

    public List<CardQueryRequest> load(String application);

    static CardLoader getFileLoader() {
        return new FileCardLoader();
    }

    static CardLoader getGithubLoader(final String githubToken) {
        return new GithubCardLoader(githubToken);
    }


    default List<CardQueryRequest> loadContent(final String content, final String applicationName) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        CardFile cardFile = mapper.readValue(content, CardFile.class);
        System.out.println(cardFile.commonQuery);
        return cardFile.queries().stream().map(convertToQueryRequest(cardFile, applicationName)).toList();
    }

    private Function<CardQuery, CardQueryRequest> convertToQueryRequest(final CardFile cardFile, final String appName) {
        return q -> new CardQueryRequest(
                appName, q.id(), q.description(), q.type(), q.query.replace("<common_query>", cardFile.commonQuery())
        );
    }

    @JsonSerialize
    record CardFile(@JsonProperty("common_query") String commonQuery, List<CardQuery> queries) {

    }

    @JsonSerialize
    record CardQuery(String id, String description, CardType type, String query) {

    }
}
