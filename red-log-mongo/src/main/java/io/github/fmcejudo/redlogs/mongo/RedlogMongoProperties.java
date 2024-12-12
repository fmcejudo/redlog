package io.github.fmcejudo.redlogs.mongo;

import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AutoConfiguration
@ConditionalOnRedlogEnabled
@ConfigurationProperties(prefix = "redlog.writer.mongo")
public class RedlogMongoProperties {

  private String url;

  private String username;

  private String password;

  private String database;

  private String collectionNamePrefix;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getCollectionNamePrefix() {
    return collectionNamePrefix;
  }

  public void setCollectionNamePrefix(String collectionNamePrefix) {
    this.collectionNamePrefix = collectionNamePrefix;
  }
}
