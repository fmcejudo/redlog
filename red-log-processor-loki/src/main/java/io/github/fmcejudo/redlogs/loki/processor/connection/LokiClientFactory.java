package io.github.fmcejudo.redlogs.loki.processor.connection;

import static org.apache.logging.log4j.util.Base64Util.encode;

import io.github.fmcejudo.redlogs.loki.processor.connection.instant.QueryInstantClient;
import io.github.fmcejudo.redlogs.loki.processor.connection.range.QueryRangeClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@FunctionalInterface
public interface LokiClientFactory {

  public enum QueryTypeEnum {
    RANGE {
      @Override
      public LokiClient createLokiClient(RestClient restClient) {
        return new QueryRangeClient(restClient);
      }
    }, INSTANT {
      @Override
      public LokiClient createLokiClient(RestClient restClient) {
        return new QueryInstantClient(restClient);
      }
    };

    public abstract LokiClient createLokiClient(RestClient restClient);
  }

  public abstract LokiClient get(QueryTypeEnum queryType);

  static LokiClientFactory createInstance(final LokiConnectionDetails connectionDetails) {

    RestClient restClient = RestClient.builder().baseUrl(connectionDetails.url())
        .defaultHeader(
            HttpHeaders.AUTHORIZATION,
            buildBasicAuthorizationValue(connectionDetails)
        )
        .build();

    return (queryTypeEnum) -> queryTypeEnum.createLokiClient(restClient);
  }

  private static String buildBasicAuthorizationValue(final LokiConnectionDetails connectionDetails) {
    if (Strings.isBlank(connectionDetails.user()) || Strings.isBlank(connectionDetails.password())) {
      return "";
    }
    return "Basic " + encode(String.join(":", connectionDetails.user(), connectionDetails.password()));
  }
}
