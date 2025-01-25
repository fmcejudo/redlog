package io.github.fmcejudo.redlogs.processor;

import java.util.List;
import java.util.stream.Stream;

import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

class MongoDBConfig extends AbstractMongoClientConfiguration {

  private final MongoConnectionProperties mongoConnectionProperties;

  MongoDBConfig(MongoConnectionProperties mongoConnectionProperties) {
    this.mongoConnectionProperties = mongoConnectionProperties;
  }

  @Override
  protected String getDatabaseName() {
    return mongoConnectionProperties.database();
  }

  @Override
  protected void configureClientSettings(Builder builder) {

    List<ServerAddress> hosts = Stream.of(mongoConnectionProperties.host().split(","))
        .map(host -> new ServerAddress(host, mongoConnectionProperties.port())).toList();

    if (StringUtils.isNotBlank(mongoConnectionProperties.user())) {
      builder.credential(MongoCredential.createCredential(
          mongoConnectionProperties.user(), mongoConnectionProperties.database(), mongoConnectionProperties.pass().toCharArray())
      );
    }
    builder.applyToClusterSettings(b -> b.hosts(hosts));
  }

  MongoTemplate mongoTemplate() {
    return new MongoTemplate(this.mongoDbFactory());
  }
}
