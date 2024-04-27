package com.github.fmcejudo.redlogs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redlog.file", ignoreInvalidFields = true)
public class RedLogFileProperties {

    private String filesPath;

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }
}
