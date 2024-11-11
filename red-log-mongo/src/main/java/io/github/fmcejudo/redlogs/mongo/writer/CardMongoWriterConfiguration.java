package io.github.fmcejudo.redlogs.mongo.writer;

import java.util.Optional;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.fmcejudo.redlogs.card.writer.CardExecutionWriter;
import io.github.fmcejudo.redlogs.card.writer.CardReportWriter;
import io.github.fmcejudo.redlogs.mongo.RedlogMongoProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class CardMongoWriterConfiguration {

  @Bean(destroyMethod = "close")
  @ConditionalOnProperty(name = "redlog.writer", havingValue = "mongo")
  @Qualifier("redlogMongoClient")
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
  @ConditionalOnProperty(name = "redlog.writer", havingValue = "mongo")
  @Qualifier("redlogMongoTemplate")
  MongoTemplate redlogMongoTemplate(final MongoClient redlogMongoClient, final RedlogMongoProperties redlogMongoProperties) {
    String database = getDatabaseName(new ConnectionString(redlogMongoProperties.getUrl()));
    return new MongoTemplate(new SimpleMongoClientDatabaseFactory(redlogMongoClient, database));
  }

  private String getDatabaseName(final ConnectionString connectionString) {
    return Optional.ofNullable(connectionString.getDatabase()).orElseThrow(() -> new IllegalStateException("database name is required"));
  }

  @Bean
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  CardExecutionWriter cardExecutionWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate redlogMongoTemplate) {
    return new CardExecutionMongoWriter(redlogMongoTemplate);
  }

  @Bean
  @ConditionalOnBean(value = MongoTemplate.class, name = "redlogMongoTemplate")
  CardReportWriter cardReportWriter(@Qualifier("redlogMongoTemplate") final MongoTemplate redlogMongoTemplate) {
    return new CardReportMongoWriter(redlogMongoTemplate);
  }

}
