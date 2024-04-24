package com.github.fmcejudo.redlogs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RedLogsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedLogsApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(MongoProperties properties) {
        return args -> {
            if (properties != null) {
                System.out.println("properties: " + properties.determineUri());
            } else {
                System.err.println("no mongo properties provided");
            }
        };
    }

}
