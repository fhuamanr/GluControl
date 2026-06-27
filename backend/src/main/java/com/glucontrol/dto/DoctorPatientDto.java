package com.glucontrol.dto;

import java.time.Instant;

public record DoctorPatientDto(Long id, String fullName, String email, Integer age, String diabetesType,
                               Integer lastGlucose, String status, Instant lastClinicalAt) {}

