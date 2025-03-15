package io.github.fmcejudo.redlogs.loki.processor.connection;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public record LokiConnectionDetails(String url, String user, String password, String token, String datasource,
                                    String dashboardUrl, Map<String, String> headers) {

  public static LokiConnectionDetails from(final Map<String, String> details) {
    if (!details.containsKey("url")) {
      throw new RuntimeException("There is no enough details to connect to loki");
    }
    String url = details.get("url");
    String user = details.getOrDefault("user", "");
    String pass = details.getOrDefault("pass", "");
    String datasource = details.get("datasource");
    String dashboardUrl = details.get("dashboard-url");
    String token = details.get("token");

    Map<String, String> headers = details.entrySet().stream().filter(e -> e.getKey().startsWith("headers"))
        .map(e -> Map.entry(e.getKey().replace("headers.", ""), e.getValue()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    return new LokiConnectionDetails(url, user, pass, token, datasource, dashboardUrl, headers);
  }

}
