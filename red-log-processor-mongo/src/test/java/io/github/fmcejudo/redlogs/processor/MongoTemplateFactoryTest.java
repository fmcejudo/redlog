package io.github.fmcejudo.redlogs.processor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

class MongoTemplateFactoryTest {

  @Test
  void shouldCreateMultipleMongoTemplateConnections() {
    //Given
    MongoConnectionPropertiesGenerator mongoConnectionPropertiesGenerator =
        MongoConnectionPropertiesGenerator.withConnectionDetails("localhost", 27017).withCredentials("user", "pass");

    MongoTemplateFactory mongoTemplateFactory = MongoTemplateFactory.init(
        Map.of(
            "default", mongoConnectionPropertiesGenerator.withDBName("default").get(),
            "secondary", mongoConnectionPropertiesGenerator.withDBName("secondary").get())
    );

    //When
    Optional<MongoTemplate> defaultMongoTemplateOpt = mongoTemplateFactory.find("default");
    Optional<MongoTemplate> secondaryMongoTemplateOpt = mongoTemplateFactory.find("secondary");

    //Then
    Assertions.assertThat(defaultMongoTemplateOpt).isPresent();
    Assertions.assertThat(secondaryMongoTemplateOpt).isPresent();
  }

  @Test
  void shouldFailWithEmptyConnectionProperties() {
    //Given && When
    Exception exception = Assertions.catchException(() -> MongoTemplateFactory.init(Map.of()));

    //Then
    Assertions.assertThat(exception)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("at least requires a mongo configuration properties to connect to");
  }

  @Test
  void shouldNotFindAConnection() {
    //Given
    MongoConnectionPropertiesGenerator mongoConnectionPropertiesGenerator =
        MongoConnectionPropertiesGenerator.withConnectionDetails("localhost", 27017).withCredentials("user", "pass");

    MongoTemplateFactory mongoTemplateFactory = MongoTemplateFactory.init(
        Map.of("default", mongoConnectionPropertiesGenerator.withDBName("default").get())
    );

    //When
    Optional<MongoTemplate> secondaryMongoTemplateOpt = mongoTemplateFactory.find("secondary");

    //Then
    Assertions.assertThat(secondaryMongoTemplateOpt).isNotPresent();
  }
}

@FunctionalInterface
interface MongoConnectionPropertiesGenerator extends Supplier<MongoConnectionProperties> {

  public static MongoConnectionPropertiesGenerator withConnectionDetails(String host, int port) {
    return () -> {
      Map<String, String> connectionsDetails = Map.of(
          "host", host,
          "port", String.valueOf(port),
          "user", "",
          "pass", "",
          "database", "test"
      );
      return MongoConnectionProperties.from(connectionsDetails);
    };
  }

  public default MongoConnectionPropertiesGenerator withCredentials(String user, String pass) {
    return () -> {
      MongoConnectionProperties mcp = this.get();
      Map<String, String> details = Map.of(
          "host", mcp.host(),
          "port", String.valueOf(mcp.port()),
          "user", user,
          "pass", pass,
          "database", mcp.database()
      );
      return MongoConnectionProperties.from(details);
    };
  }

  public default MongoConnectionPropertiesGenerator withDBName(String databaseName) {
    return () -> {
      MongoConnectionProperties mcp = this.get();
      Map<String, String> details = Map.of(
          "host", mcp.host(),
          "port", String.valueOf(mcp.port()),
          "user", mcp.user(),
          "pass", mcp.pass(),
          "database", databaseName
      );
      return MongoConnectionProperties.from(details);
    };
  }

}