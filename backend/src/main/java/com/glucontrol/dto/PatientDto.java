package com.glucontrol.dto;
import jakarta.validation.constraints.*;
import java.time.*;
public record PatientDto(Long id, Long userId, String fullName, String email, @NotBlank String documentNumber,
 LocalDate birthDate, String phone, String diabetesType, @Min(40) Integer glucoseTargetMin,
 @Max(400) Integer glucoseTargetMax, String emergencyContact) {}

