package com.glucontrol.controller;

import com.glucontrol.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
public class UploadController {
  private final FileStorageService storage;

  @PostMapping(value = "/meals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String,String> uploadMeal(@RequestPart("file") MultipartFile file) {
    String filename = storage.storeMealImage(file);
    return Map.of("url", "/api/uploads/" + filename);
  }

  @GetMapping("/{filename:.+}")
  public ResponseEntity<Resource> image(@PathVariable String filename) {
    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(storage.contentType(filename)))
      .cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(7)).cachePublic())
      .body(storage.load(filename));
  }
}

