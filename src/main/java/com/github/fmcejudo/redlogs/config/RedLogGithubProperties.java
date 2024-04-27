package com.github.fmcejudo.redlogs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "redlog.github")
public class RedLogGithubProperties {

    private String githubToken;
    private Map<String, String> urlMapper;

    public Map<String, String> getUrlMapper() {
        return urlMapper;
    }

    public void setUrlMapper(Map<String, String> urlMapper) {
        this.urlMapper = urlMapper;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }
}
