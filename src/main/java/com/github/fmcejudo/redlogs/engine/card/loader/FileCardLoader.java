package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import com.github.fmcejudo.redlogs.engine.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class FileCardLoader implements CardLoader {

    private final Resource resource;

    public FileCardLoader(final RedLogFileProperties redLogFileProperties) {
        this.resource = new DefaultResourceLoader().getResource(redLogFileProperties.getFilesPath());
    }

    @Override
    public List<CardQueryRequest> load(final String application, final CardConverter converter) {
        try {
            File file = resource.createRelative(application.toUpperCase() + ".yaml").getFile();
            String content = new String(Files.readAllBytes(file.toPath()));
            return converter.convert(content, application);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
