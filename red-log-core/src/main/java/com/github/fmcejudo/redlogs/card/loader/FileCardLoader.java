package com.github.fmcejudo.redlogs.card.loader;

import java.io.File;
import java.nio.file.Files;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import com.github.fmcejudo.redlogs.card.exception.ReplacementException;
import com.github.fmcejudo.redlogs.config.RedLogFileProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class FileCardLoader extends AbstractCardFileLoader {

  private final Resource resource;

  public FileCardLoader(final RedLogFileProperties redLogFileProperties) {
    this.resource = new DefaultResourceLoader().getResource(redLogFileProperties.getFilesPath());
  }

  @Override
  public CardFile load(final CardContext cardContext) {

    String application = cardContext.applicationName();
    try {
      File file = resource.createRelative(application.toUpperCase() + ".yaml").getFile();
      String content = new String(Files.readAllBytes(file.toPath()));
      return this.load(content, cardContext);
    } catch (ReplacementException e) {
      throw e;
    } catch (Exception e) {
      throw new CardExecutionException(e.getMessage());
    }
  }

}
