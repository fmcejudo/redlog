package com.github.fmcejudo.redlogs.card.loader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardFileLoadException;
import com.github.fmcejudo.redlogs.card.exception.ReplacementException;
import com.github.fmcejudo.redlogs.config.RedLogGithubProperties;

class GithubCardLoader extends AbstractCardFileLoader {

  public final Map<String, String> urlMapper;

  private final GithubClient githubClient;

  public GithubCardLoader(final RedLogGithubProperties redLogGithubProperties, GithubClient githubClient) {
    this.githubClient = githubClient;
    this.urlMapper = redLogGithubProperties.getUrlMapper();
  }

  @Override
  public CardFile load(final CardContext cardContext) {

    String application = cardContext.applicationName();
    try {
      String filePath = repoUrl(application);
      String content = githubClient.download(filePath);
      return this.load(content, cardContext);
    } catch (ReplacementException e) {
      throw e;
    } catch (Exception e) {
      throw new CardFileLoadException(
          "it could not load from github file with reference: '" + cardContext.applicationName() + "', with error " + e.getMessage()
      );
    }
  }

  private String repoUrl(final String application) {
    return urlMapper.entrySet().stream()
        .filter(e -> application.equalsIgnoreCase(e.getKey()))
        .findFirst()
        .orElseThrow().getValue();
  }

  @FunctionalInterface
  interface GithubClient {

    String download(String url);
  }

  static class DefaultGithubClient implements GithubClient {

    private final String token;

    public DefaultGithubClient(final String token) {
      this.token = token;
    }

    @Override
    public String download(String url) {
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
          throw new CardFileLoadException("Path to file is not available, check the path " + url);
        }
        return response.body();
      } catch (Exception e) {
        throw new CardFileLoadException(e.getMessage());
      }

    }

  }
}
