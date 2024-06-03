package com.github.fmcejudo.redlogs.card.engine.loader;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.converter.CardConverter;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryRequest;
import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FileCardLoader implements CardLoader {

    private final Resource resource;

    private final CardConverter cardConverter;

    public FileCardLoader(final RedLogFileProperties redLogFileProperties, final CardConverter cardConverter) {
        this.resource = new DefaultResourceLoader().getResource(redLogFileProperties.getFilesPath());
        this.cardConverter = cardConverter;
    }

    @Override
    public List<CardQueryRequest> load(final CardContext cardContext) {

        String application = cardContext.applicationName();
        LocalDate reportDate = cardContext.reportDate();
        try {
            File file = resource.createRelative(application.toUpperCase() + ".yaml").getFile();
            String content = new String(Files.readAllBytes(file.toPath()));
            //TODO: update content, replacing parameters applied in context.
            return cardConverter.convert(content, application).stream()
                    .map(s -> s.withReportDate(reportDate))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
