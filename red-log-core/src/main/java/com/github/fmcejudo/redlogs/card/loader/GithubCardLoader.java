package com.github.fmcejudo.redlogs.card.loader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;

class GithubCardLoader implements CardLoader {

    public final Map<String, String> urlMapper;

    private final GithubClient githubClient;

    private final CardConverter cardConverter;

    public GithubCardLoader(final RedLogGithubProperties redLogGithubProperties, final CardConverter cardConverter) {
        this.githubClient = new GithubClient(redLogGithubProperties.getGithubToken());
        this.urlMapper = redLogGithubProperties.getUrlMapper();
        this.cardConverter = cardConverter;
    }

    @Override
    public CardRequest load(final CardContext cardContext) {

        String application = cardContext.applicationName();
        try {
            String filePath = repoUrl(application);
            String content = githubClient.download(filePath);
            return cardConverter.convert(content, cardContext);
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
                        .header("Accept", "application/vnd.github.raw+json")
                        .header("X-GitHub-Api-Version", "2022-11-28")
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
