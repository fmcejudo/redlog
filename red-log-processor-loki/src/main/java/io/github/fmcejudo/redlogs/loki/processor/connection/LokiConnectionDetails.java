package io.github.fmcejudo.redlogs.loki.processor.connection;

import java.util.Map;

public record LokiConnectionDetails(String url, String user, String password, String token, String datasource, String dashboardUrl) {

  public static LokiConnectionDetails from(final Map<String, String> details) {
    if (!details.containsKey("url")) {
      throw new RuntimeException("There is no enough details to connect to loki");
    }
    String url = details.get("url");
    String user = details.getOrDefault("user","");
    String pass = details.getOrDefault("pass","");
    String datasource = details.get("datasource");
    String dashboardUrl = details.get("dashboardUrl");
    String token = details.get("token");
    return new LokiConnectionDetails(url, user, pass, token, datasource, dashboardUrl);
  }

}
