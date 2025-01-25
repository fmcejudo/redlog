package io.github.fmcejudo.redlogs.processor;

import java.util.Map;
import java.util.Optional;

class MongoConnectionProperties {

  private final String host;
  private final Integer port;
  private final String user;
  private final String pass;
  private final String database;

  private MongoConnectionProperties(Map<String, String> connectionDetails) {
    this.database = connectionDetails.get("database");
    this.host = connectionDetails.get("host");
    this.port = Optional.ofNullable(connectionDetails.get("port")).map(Integer::parseInt).orElse(27017);
    this.user = connectionDetails.get("user");
    this.pass = connectionDetails.get("pass");
  }

  public static MongoConnectionProperties from(Map<String, String> connectionDetails) {
    return new MongoConnectionProperties(connectionDetails);
  }

  public String database() {
    return database;
  }

  public String host() {
    return host;
  }

  public String pass() {
    return pass;
  }

  public Integer port() {
    return port;
  }

  public String user() {
    return user;
  }
}
