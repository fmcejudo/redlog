package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;
import com.github.fmcejudo.redlogs.engine.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

class GithubCardLoader implements CardLoader {

    public final Map<String, String> urlMapper;

    private final GithubClient githubClient;

    public GithubCardLoader(final RedLogGithubProperties redLogGithubProperties) {
        this.githubClient = new GithubClient(redLogGithubProperties.getGithubToken());
        this.urlMapper = redLogGithubProperties.getUrlMapper();
    }

    @Override
    public List<CardQueryRequest> load(String application, final CardConverter cardConverter) {
        try {
            String content = githubClient.download(repoUrl(application) + application + ".yaml");
            return cardConverter.convert(content, application);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String repoUrl(final String application) {
        return urlMapper.entrySet().stream()
                .filter(e -> application.startsWith(e.getKey()))
                .findFirst()
                .orElseThrow().getValue();
    }

    static class GithubClient {

        private final String token;

        public GithubClient(final String token) {
            this.token = token;
        }

        String download(String url) {
            try (HttpClient httpClient = HttpClient.newHttpClient()) {

                // Create a request to get the file content
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .build();

                // Send the request and get the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 404) {
                    throw new RuntimeException("Path to file is not available, check the path");
                }
                return response.body();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }
}
