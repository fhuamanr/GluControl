package com.glucontrol.dto;
import com.glucontrol.entity.GlucoseMeasurement.Context;
import jakarta.validation.constraints.*;
import java.time.Instant;
public record GlucoseDto(Long id, @NotNull Long patientId, @NotNull @Min(20) @Max(600) Integer valueMgDl,
 @NotNull Instant measuredAt, @NotNull Context context, @Size(max=500) String notes, String rangeStatus) {}

