package com.glucontrol.dto;
import com.glucontrol.entity.Alert.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
public record AlertDto(Long id, @NotNull Long patientId, @NotNull AlertType type, @NotNull Severity severity,
 @NotBlank String title, @NotBlank String message, @NotNull Instant occurredAt, boolean acknowledged) {}

