package com.github.fmcejudo.redlogs.engine.card.loader;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryRequest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class FileCardLoader implements CardLoader {

    private static final String PATH_TO_CARDS = "cards/";


    public FileCardLoader() {
    }

    @Override
    public List<CardQueryRequest> load(final String application) {
        try {
            File file = new ClassPathResource(PATH_TO_CARDS + application.toUpperCase() + ".yaml").getFile();
            String content = new String(Files.readAllBytes(file.toPath()));
            return loadContent(content, application);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
