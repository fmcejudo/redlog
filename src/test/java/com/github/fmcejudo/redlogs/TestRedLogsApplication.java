package com.github.fmcejudo.redlogs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;


@TestConfiguration(proxyBeanMethods = false)
public class TestRedLogsApplication {

    @Bean
    @RestartScope
    @ServiceConnection
    MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:5"));
    }

    @Bean
    ApplicationRunner runner(final MongoDBContainer mongoDBContainer) {
        return args -> {
            System.out.println(mongoDBContainer.getReplicaSetUrl());
        };
    }

    public static void main(String[] args) {
        String[] configurationArguments = {"--spring.cloud.vault.port=8200"};
        SpringApplication.from(RedLogsApplication::main)
                .with(TestRedLogsApplication.class)
                .run(configurationArguments);
    }

}