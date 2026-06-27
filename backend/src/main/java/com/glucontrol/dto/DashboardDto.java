package com.glucontrol.dto;
import java.util.List;
public record DashboardDto(long activePatients, long criticalAlerts, long pendingAlerts, Integer averageGlucose,
                           List<DoctorPatientDto> patients) {}
