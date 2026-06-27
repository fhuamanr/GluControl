package com.glucontrol.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record ReportDto(Long id, @NotNull Long patientId, @NotNull LocalDate periodStart, @NotNull LocalDate periodEnd,
 Integer averageGlucose, Integer readingsInRangePercent, String summary, String documentUrl) {}

