package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

class GithubCardLoader implements CardLoader {

    public static final Map<String, String> URL_MAPPER = Map.of(
            "ALERTAPI", "https://raw.githubusercontent.com/inditex/mic-alerthubapi/shifts/shifts/",
            "ALERTHUB", "https://raw.githubusercontent.com/inditex/wsc-alerthub/shifts/shifts/"
    );

    private final GithubClient githubClient;

    public GithubCardLoader(final String githubToken) {
        this.githubClient = new GithubClient(githubToken);
    }

    @Override
    public List<CardQueryRequest> load(String application) {
        try {
            String content = githubClient.download(repoUrl(application) + application + ".yaml");
            System.out.println(content);
            return loadContent(content, application);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String repoUrl(final String application) {
        return URL_MAPPER.entrySet().stream()
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
