package com.github.fmcejudo.redlogs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redlog.mongo")
public class RedLogMongoProperties {

    private String collectionNamePrefix;

    public String getCollectionNamePrefix() {
        return collectionNamePrefix;
    }

    public void setCollectionNamePrefix(String collectionNamePrefix) {
        this.collectionNamePrefix = collectionNamePrefix;
    }

}
