package com.github.fmcejudo.redlogs.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    RedLogConfigProperties.class
})
@ConfigurationPropertiesScan(basePackageClasses = RedLogConfigProperties.class)
@TestPropertySource(properties = {
    "redlog.writer.type=mongo",
    "redlog.source.type=file",
    "redlog.source.file.files-path=file://file.path",
    "redlog.processor.loki.url=http://loki-url.io"
})
class RedLogConfigPropertiesTest {

  @Autowired
  RedLogConfigProperties redLogConfigProperties;

  @Test
  void shouldReadProcessorProperties() {
    //Given && When && Then
    Assertions.assertThat(redLogConfigProperties.getProcessor()).containsEntry("loki.url","http://loki-url.io");
  }

  @Test
  void shouldReadSourceFileConfig() {
    //Given && When && Then
    Assertions.assertThat(redLogConfigProperties.getSource()).satisfies(source -> {
      Assertions.assertThat(source.getType()).isEqualTo("file");
      Assertions.assertThat(source.getFile()).satisfies(file -> {
        Assertions.assertThat(file.getFilesPath()).isEqualTo("file://file.path");
      });
    });
  }

}