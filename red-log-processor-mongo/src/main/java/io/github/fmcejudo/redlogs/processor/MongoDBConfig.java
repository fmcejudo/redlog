package io.github.fmcejudo.redlogs.processor;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
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

    if (StringUtils.isNotBlank(mongoConnectionProperties.user())) {
      builder.credential(MongoCredential.createCredential(
          mongoConnectionProperties.user(), mongoConnectionProperties.database(), mongoConnectionProperties.pass().toCharArray())
      );
    }
    builder.applyToClusterSettings(b -> b.applyConnectionString(new ConnectionString(mongoConnectionProperties.url())));
  }

  MongoTemplate mongoTemplate() {
    return new MongoTemplate(this.mongoDbFactory());
  }
}
