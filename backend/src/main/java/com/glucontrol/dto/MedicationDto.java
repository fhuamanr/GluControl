package com.glucontrol.dto;
import jakarta.validation.constraints.*;
import java.time.*;
public record MedicationDto(Long id, @NotNull Long patientId, @NotBlank String name, @NotBlank String dose,
 String frequency, LocalTime reminderTime, LocalDate startDate, LocalDate endDate, boolean active,
 @Size(max=500) String instructions) {}

