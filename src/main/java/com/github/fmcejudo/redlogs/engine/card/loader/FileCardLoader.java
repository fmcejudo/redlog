package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import com.github.fmcejudo.redlogs.engine.card.converter.CardConverter;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

public class FileCardLoader implements CardLoader {

    private final Resource resource;

    private final CardConverter cardConverter;

    public FileCardLoader(final RedLogFileProperties redLogFileProperties, final CardConverter cardConverter) {
        this.resource = new DefaultResourceLoader().getResource(redLogFileProperties.getFilesPath());
        this.cardConverter = cardConverter;
    }

    @Override
    public List<CardQueryRequest> load(final String application, final LocalDate reportDate) {
        try {
            File file = resource.createRelative(application.toUpperCase() + ".yaml").getFile();
            String content = new String(Files.readAllBytes(file.toPath()));
            return cardConverter.convert(content, application).stream().map(s -> s.withReportDate(reportDate)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
