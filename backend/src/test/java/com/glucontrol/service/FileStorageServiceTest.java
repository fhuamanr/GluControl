package com.glucontrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class FileStorageServiceTest {
  @TempDir Path directory;

  @Test void storesAllowedImageWithOpaqueName() throws Exception {
    FileStorageService storage=new FileStorageService(directory.toString(),1024);storage.init();
    String name=storage.storeMealImage(new MockMultipartFile("file","plato.jpg","image/jpeg",new byte[]{1,2,3}));
    assertThat(name).endsWith(".jpg");assertThat(storage.load(name).exists()).isTrue();
  }

  @Test void rejectsUnsupportedContentType() throws Exception {
    FileStorageService storage=new FileStorageService(directory.toString(),1024);storage.init();
    assertThatThrownBy(()->storage.storeMealImage(new MockMultipartFile("file","nota.pdf","application/pdf",new byte[]{1})))
      .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Formato no permitido");
  }

  @Test void rejectsImageOverConfiguredLimit() throws Exception {
    FileStorageService storage=new FileStorageService(directory.toString(),1);storage.init();
    assertThatThrownBy(()->storage.storeMealImage(new MockMultipartFile("file","plato.jpg","image/jpeg",new byte[]{1,2})))
      .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("10 MB");
  }
}
