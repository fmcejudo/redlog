package com.github.fmcejudo.redlogs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redlog")
public class RedLogConfigProperties {

    private SourceType sourceType;

    private RedLogGithubProperties github;

    private RedLogMongoProperties mongo;


    public RedLogGithubProperties getGithub() {
        return github;
    }

    public void setGithub(RedLogGithubProperties github) {
        this.github = github;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public RedLogMongoProperties getMongo() {
        return mongo;
    }

    public void setMongo(RedLogMongoProperties mongo) {
        this.mongo = mongo;
    }
}
