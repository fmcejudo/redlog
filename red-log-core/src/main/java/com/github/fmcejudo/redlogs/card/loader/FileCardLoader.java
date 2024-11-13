package com.github.fmcejudo.redlogs.card.loader;

import java.io.File;
import java.nio.file.Files;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import io.github.fmcejudo.redlogs.card.domain.CardRequest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class FileCardLoader implements CardLoader {

    private final Resource resource;

    private final CardConverter cardConverter;

    public FileCardLoader(final RedLogFileProperties redLogFileProperties, final CardConverter cardConverter) {
        this.resource = new DefaultResourceLoader().getResource(redLogFileProperties.getFilesPath());
        this.cardConverter = cardConverter;
    }

    @Override
    public CardRequest load(final CardContext cardContext) {

        String application = cardContext.applicationName();
        try {
            File file = resource.createRelative(application.toUpperCase() + ".yaml").getFile();
            String content = new String(Files.readAllBytes(file.toPath()));
            return cardConverter.convert(content, cardContext);
        } catch (Exception e) {
            throw new CardExecutionException(e.getMessage());
        }
    }

}
