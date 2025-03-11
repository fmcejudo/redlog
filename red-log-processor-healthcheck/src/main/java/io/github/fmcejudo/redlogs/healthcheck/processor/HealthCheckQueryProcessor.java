package io.github.fmcejudo.redlogs.healthcheck.processor;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.card.CardQueryResponse;
import io.github.fmcejudo.redlogs.card.CardQueryResponseEntry;
import io.github.fmcejudo.redlogs.card.processor.CardQueryProcessor;
import io.github.fmcejudo.redlogs.healthcheck.card.HealthCheckQueryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

public interface HealthCheckQueryProcessor extends CardQueryProcessor {

  static HealthCheckQueryProcessor createInstance() {
    RestClient.Builder builder = RestClient.builder().defaultHeaders(httpHeaders -> httpHeaders.add(CONTENT_TYPE, APPLICATION_JSON_VALUE));
    return cardQueryRequest -> {
      Assert.isInstanceOf(
          HealthCheckQueryRequest.class,
          cardQueryRequest,
          "Healthcheck processor only expects " + HealthCheckQueryRequest.class.getSimpleName()
      );
      HealthCheckQueryRequest hcqr = (HealthCheckQueryRequest) cardQueryRequest;

      ResponseEntity<HealthCheck> response = queryUrl(builder, hcqr);

      if (!response.getStatusCode().is2xxSuccessful()) {
        return CardQueryResponse.from(cardQueryRequest).failure("url returned status " + response.getStatusCode());
      }

      HealthCheck healthCheck = response.getBody();

      if (healthCheck == null) {
        return CardQueryResponse.from(cardQueryRequest).failure("body does not match");
      }

      List<CardQueryResponseEntry> entries = new ArrayList<>();
      if ("down".equalsIgnoreCase(healthCheck.status())) {
        CardQueryResponseEntry cardQueryResponseEntry =
            new CardQueryResponseEntry(Map.of("status", "down", "url", hcqr.url()), 1);
        entries.add(cardQueryResponseEntry);
      }

      return CardQueryResponse.from(cardQueryRequest).success(hcqr.url(), entries);
    };
  }

  private static ResponseEntity<HealthCheck> queryUrl(Builder builder, HealthCheckQueryRequest hcqr) {
    try {
      HealthCheck healthCheck = builder.baseUrl(hcqr.url()).build().get().retrieve().body(HealthCheck.class);
      return ResponseEntity.ok(healthCheck);
    } catch (HttpClientErrorException e) {
      return ResponseEntity.status(e.getStatusCode()).body(null);
    }
  }
}

record HealthCheck(String status) {

}
