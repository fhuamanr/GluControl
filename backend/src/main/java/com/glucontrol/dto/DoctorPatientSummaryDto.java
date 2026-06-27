package com.glucontrol.dto;

import java.time.Instant;

public record DoctorPatientSummaryDto(PatientDto patient, Integer age, Integer averageGlucose,
                                      Integer readingsInRangePercent, long activeMedications,
                                      long pendingAlerts, Instant lastClinicalAt) {}

