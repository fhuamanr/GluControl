package com.glucontrol.mapper;

import com.glucontrol.dto.*;
import com.glucontrol.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {
  public PatientDto patient(Patient p) { return new PatientDto(p.getId(),p.getUser().getId(),p.getUser().getFullName(),p.getUser().getEmail(),p.getDocumentNumber(),p.getBirthDate(),p.getPhone(),p.getDiabetesType(),p.getGlucoseTargetMin(),p.getGlucoseTargetMax(),p.getEmergencyContact()); }
  public GlucoseDto glucose(GlucoseMeasurement g) { int v=g.getValueMgDl(); return new GlucoseDto(g.getId(),g.getPatient().getId(),v,g.getMeasuredAt(),g.getContext(),g.getNotes(),v<70?"LOW":v>180?"HIGH":"IN_RANGE"); }
  public MealDto meal(Meal m) { return new MealDto(m.getId(),m.getPatient().getId(),m.getName(),m.getMealType(),m.getEatenAt(),m.getCarbohydratesGrams(),m.getCalories(),m.getPhotoUrl(),m.getNotes()); }
  public MedicationDto medication(Medication m) { return new MedicationDto(m.getId(),m.getPatient().getId(),m.getName(),m.getDose(),m.getFrequency(),m.getReminderTime(),m.getStartDate(),m.getEndDate(),m.isActive(),m.getInstructions()); }
  public AlertDto alert(Alert a) { return new AlertDto(a.getId(),a.getPatient().getId(),a.getType(),a.getSeverity(),a.getTitle(),a.getMessage(),a.getOccurredAt(),a.isAcknowledged()); }
  public ReportDto report(MedicalReport r) { return new ReportDto(r.getId(),r.getPatient().getId(),r.getPeriodStart(),r.getPeriodEnd(),r.getAverageGlucose(),r.getReadingsInRangePercent(),r.getSummary(),r.getDocumentUrl()); }
  public SettingsDto settings(UserSettings s) { return new SettingsDto(s.getId(),s.getUser().getId(),s.isGlucoseAlerts(),s.isMedicationReminders(),s.isMealReminders(),s.getLocale(),s.getTimezone()); }
}

