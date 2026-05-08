package io.github.fmcejudo.redlogs.processor;

import java.util.Map;

class MongoConnectionProperties {

  private final String url;

  private final String user;

  private final String pass;

  private final String database;

  private MongoConnectionProperties(Map<String, String> connectionDetails) {
    this.database = connectionDetails.get("database");
    this.url = connectionDetails.get("url");
    this.user = connectionDetails.get("user");
    this.pass = connectionDetails.get("pass");
  }

  public static MongoConnectionProperties from(Map<String, String> connectionDetails) {
    return new MongoConnectionProperties(connectionDetails);
  }

  public String database() {
    return database;
  }

  public String url() {
    return url;
  }

  public String pass() {
    return pass;
  }

  public String user() {
    return user;
  }
}
