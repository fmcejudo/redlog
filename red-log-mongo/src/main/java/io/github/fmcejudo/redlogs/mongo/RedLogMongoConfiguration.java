package io.github.fmcejudo.redlogs.mongo;

import java.util.Optional;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.fmcejudo.redlogs.annotation.ConditionalOnRedlogEnabled;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@AutoConfiguration
public class RedLogMongoConfiguration {

  @Bean(destroyMethod = "close")
  @ConditionalOnRedlogEnabled
  @ConditionalOnProperty(name = "redlog.writer.type", havingValue = "mongo")
  @ConditionalOnMissingBean(value = MongoClient.class)
  MongoClient redlogMongoClient(final RedlogMongoProperties redlogMongoProperties) {

    Builder builder = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(redlogMongoProperties.getUrl()));

    String database = getDatabaseName(new ConnectionString(redlogMongoProperties.getUrl()));
    if (StringUtils.isBlank(database)) {
      throw new RuntimeException("There is not defined database to connect to");
    }
    if (StringUtils.isNotBlank(redlogMongoProperties.getUsername()) && StringUtils.isNotBlank(redlogMongoProperties.getPassword())) {
      builder.credential(MongoCredential.createCredential(
          redlogMongoProperties.getUsername(), database, redlogMongoProperties.getPassword().toCharArray())
      );
    }
    return MongoClients.create(builder.build());
  }

  @Bean
  @ConditionalOnRedlogEnabled
  @ConditionalOnProperty(name = "redlog.writer.type", havingValue = "mongo")
  @ConditionalOnMissingBean(value = MongoTemplate.class)
  MongoTemplate redlogMongoTemplate(final MongoClient redlogMongoClient, final RedlogMongoProperties redlogMongoProperties) {
    String database = getDatabaseName(new ConnectionString(redlogMongoProperties.getUrl()));
    return new MongoTemplate(new SimpleMongoClientDatabaseFactory(redlogMongoClient, database));
  }

  private String getDatabaseName(final ConnectionString connectionString) {
    return Optional.ofNullable(connectionString.getDatabase()).orElseThrow(() -> new IllegalStateException("database name is required"));
  }

}
