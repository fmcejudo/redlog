package io.github.fmcejudo.redlogs.loki.processor.connection;

import static org.apache.logging.log4j.util.Base64Util.encode;

import java.util.Map;

import io.github.fmcejudo.redlogs.loki.processor.connection.instant.QueryInstantClient;
import io.github.fmcejudo.redlogs.loki.processor.connection.range.QueryRangeClient;
import org.apache.commons.lang3.StringUtils;
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
    },
    INSTANT {
      @Override
      public LokiClient createLokiClient(RestClient restClient) {
        return new QueryInstantClient(restClient);
      }
    };

    public abstract LokiClient createLokiClient(RestClient restClient);
  }

  public abstract LokiClient get(QueryTypeEnum queryType);

  static LokiClientFactory createInstance(final LokiConnectionDetails connectionDetails) {

    Map<String, String> headers = connectionDetails.headers();

    RestClient restClient = RestClient.builder().baseUrl(connectionDetails.url())
        .defaultHeader(
            HttpHeaders.AUTHORIZATION,
            buildBasicAuthorizationValue(connectionDetails)
        )
        .defaultHeaders(hc -> headers.forEach(hc::addIfAbsent))
        .build();

    return queryTypeEnum -> queryTypeEnum.createLokiClient(restClient);
  }

  private static String buildBasicAuthorizationValue(final LokiConnectionDetails connectionDetails) {
    if (Strings.isBlank(connectionDetails.user()) || Strings.isBlank(connectionDetails.password())) {
      return "";
    }
    if (StringUtils.isNotBlank(connectionDetails.token())) {
      return "Bearer " + connectionDetails.token();
    }
    return "Basic " + encode(String.join(":", connectionDetails.user(), connectionDetails.password()));
  }
}
