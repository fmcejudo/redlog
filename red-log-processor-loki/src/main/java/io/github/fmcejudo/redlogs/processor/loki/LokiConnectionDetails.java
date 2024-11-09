package io.github.fmcejudo.redlogs.processor.loki;

import java.util.Map;

record LokiConnectionDetails(String url, String user, String password, String datasource, String dashboardUrl) {

  public static LokiConnectionDetails from(final Map<String, String> details) {
    if (!details.containsKey("url") || !details.containsKey("datasource")) {
      throw new RuntimeException("There is no enough details to connect to loki");
    }
    String url = details.get("url");
    String user = details.get("user");
    String pass = details.get("pass");
    String datasource = details.get("datasource");
    String dashboardUrl = details.getOrDefault("dashboardUrl", "");
    return new LokiConnectionDetails(url, user, pass, datasource, dashboardUrl);
  }

}
