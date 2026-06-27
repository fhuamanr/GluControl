package com.glucontrol.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@Service
public class FileStorageService {
  private static final Map<String,String> EXTENSIONS = Map.of(
    "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
  private final Path root;
  private final long maxBytes;

  public FileStorageService(@Value("${app.upload.directory}") String directory,
                            @Value("${app.upload.max-bytes}") long maxBytes) {
    this.root = Paths.get(directory).toAbsolutePath().normalize();
    this.maxBytes = maxBytes;
  }

  @PostConstruct
  void init() throws IOException { Files.createDirectories(root); }

  public String storeMealImage(MultipartFile file) {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("Selecciona una imagen");
    if (file.getSize() > maxBytes) throw new IllegalArgumentException("La imagen supera el máximo de 10 MB");
    String extension = EXTENSIONS.get(file.getContentType());
    if (extension == null) throw new IllegalArgumentException("Formato no permitido. Usa JPEG, PNG o WebP");
    String filename = UUID.randomUUID() + extension;
    Path destination = root.resolve(filename).normalize();
    if (!destination.getParent().equals(root)) throw new IllegalArgumentException("Nombre de archivo inválido");
    try {
      Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
      return filename;
    } catch (IOException ex) {
      throw new IllegalArgumentException("No se pudo guardar la imagen");
    }
  }

  public Resource load(String filename) {
    Path file = root.resolve(filename).normalize();
    if (!file.getParent().equals(root) || !Files.exists(file)) throw new com.glucontrol.exception.ResourceNotFoundException("Imagen no encontrada");
    try { return new UrlResource(file.toUri()); }
    catch (MalformedURLException ex) { throw new com.glucontrol.exception.ResourceNotFoundException("Imagen no encontrada"); }
  }

  public String contentType(String filename) {
    try { return Optional.ofNullable(Files.probeContentType(root.resolve(filename))).orElse("application/octet-stream"); }
    catch (IOException ex) { return "application/octet-stream"; }
  }
}
