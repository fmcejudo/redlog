package com.github.fmcejudo.redlogs.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redlog")
public class RedLogConfigProperties {

    private RedLogSource source;

    private Map<String, String> processor;

    public RedLogSource getSource() {
        return source;
    }

    public void setSource(RedLogSource source) {
        this.source = source;
    }

    public Map<String, String> getProcessor() {
        return processor;
    }

    public void setProcessor(Map<String, String> processor) {
        this.processor = processor;
    }
}
