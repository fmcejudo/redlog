package com.github.fmcejudo.redlogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.vault.VaultContainer;


@TestConfiguration(proxyBeanMethods = false)
@EnableAsync
@EnableScheduling
public class TestRedLogsApplication {

    private static final String VAULT_TOKEN = "super-token";

    static VaultContainer<?> vaultContainer;

    private static final String PASSWORD = """
    password
    """;

    private static final String URL = "http://192.168.1.170:3100";
    private static final String USERNAME = "user";

    static {
        vaultContainer = new VaultContainer<>("hashicorp/vault:latest")
                .withVaultToken(VAULT_TOKEN)
                .withInitCommand("""
                        kv put secret/redlog loki.url="%s" loki.username=%s loki.password="%s"
                        """.formatted(URL, USERNAME, PASSWORD));
        Startables.deepStart(vaultContainer).join();
    }

    @Bean
    @ServiceConnection
    @RestartScope
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    }

    @Bean
    @RestartScope
    GenericContainer<?> vaultContainer() {
        return vaultContainer;
    }

    public static void main(String[] args) {
        String[] configurationArguments = {"--spring.cloud.vault.port=8200"};
        SpringApplication.from(RedLogsApplication::main)
                .with(TestRedLogsApplication.class)
                .run(configurationArguments);
    }

}