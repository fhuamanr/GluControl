package com.glucontrol.dto;
import java.util.List;
public record DashboardDto(long activePatients, long criticalAlerts, int pendingFollowUps, double averageHba1c,
 List<PatientRow> patients) {
  public record PatientRow(Long id, String name, String diabetesType, Integer lastGlucose, String status) {}
}

