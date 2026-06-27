package com.glucontrol.service;

import com.glucontrol.dto.*;
import com.glucontrol.entity.*;
import com.glucontrol.exception.ResourceNotFoundException;
import com.glucontrol.mapper.ApiMapper;
import com.glucontrol.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {
  private final PatientRepository patients;
  private final GlucoseMeasurementRepository glucose;
  private final MealRepository meals;
  private final MedicationRepository medications;
  private final AlertRepository alerts;
  private final ApiMapper mapper;

  public Page<DoctorPatientDto> patients(String query, Pageable pageable) {
    Page<Patient> page = query == null || query.isBlank()
      ? patients.findByUserRole(User.Role.PATIENT, pageable)
      : patients.findByUserRoleAndUserFullNameContainingIgnoreCase(User.Role.PATIENT, query, pageable);
    return page.map(this::row);
  }

  public DoctorPatientSummaryDto summary(Long patientId) {
    Patient patient = patient(patientId);
    List<GlucoseMeasurement> recent = glucose.findTop7ByPatientIdOrderByMeasuredAtDesc(patientId);
    Integer average = recent.isEmpty() ? null : (int) Math.round(recent.stream().mapToInt(GlucoseMeasurement::getValueMgDl).average().orElse(0));
    Integer inRange = recent.isEmpty() ? null : (int) Math.round(recent.stream()
      .filter(item -> item.getValueMgDl() >= 70 && item.getValueMgDl() <= 180).count() * 100.0 / recent.size());
    return new DoctorPatientSummaryDto(mapper.patient(patient), age(patient), average, inRange,
      medications.countByPatientIdAndActiveTrue(patientId), alerts.countByPatientIdAndAcknowledgedFalse(patientId), lastClinicalAt(patient));
  }

  public Page<GlucoseDto> measurements(Long patientId, Pageable pageable) {
    patient(patientId);
    return glucose.findByPatientIdOrderByMeasuredAtDesc(patientId, pageable).map(mapper::glucose);
  }

  public Page<MealDto> meals(Long patientId, Pageable pageable) {
    patient(patientId);
    return meals.findByPatientIdOrderByEatenAtDesc(patientId, pageable).map(mapper::meal);
  }

  public List<MedicationDto> medications(Long patientId) {
    patient(patientId);
    return medications.findByPatientIdOrderByActiveDescNameAsc(patientId).stream().map(mapper::medication).toList();
  }

  public Page<AlertDto> alerts(Long patientId, Pageable pageable) {
    patient(patientId);
    return alerts.findByPatientIdOrderByOccurredAtDesc(patientId, pageable).map(mapper::alert);
  }

  public DashboardDto dashboard() {
    List<DoctorPatientDto> rows = patients.findByUserRole(User.Role.PATIENT,
      PageRequest.of(0, 8, Sort.by("id").descending())).stream().map(this::row).toList();
    List<Integer> values = rows.stream().map(DoctorPatientDto::lastGlucose).filter(Objects::nonNull).toList();
    Integer average = values.isEmpty() ? null : (int) Math.round(values.stream().mapToInt(Integer::intValue).average().orElse(0));
    return new DashboardDto(patients.count(), alerts.countByAcknowledgedFalseAndSeverity(Alert.Severity.CRITICAL),
      alerts.countByAcknowledgedFalse(), average, rows);
  }

  private DoctorPatientDto row(Patient patient) {
    Integer last = glucose.findFirstByPatientIdOrderByMeasuredAtDesc(patient.getId())
      .map(GlucoseMeasurement::getValueMgDl).orElse(null);
    long pending = alerts.countByPatientIdAndAcknowledgedFalse(patient.getId());
    String status = last == null ? "SIN_DATOS" : last < 70 || last > 180 ? "ATENCION" : pending > 0 ? "SEGUIMIENTO" : "ESTABLE";
    return new DoctorPatientDto(patient.getId(), patient.getUser().getFullName(), patient.getUser().getEmail(), age(patient),
      patient.getDiabetesType(), last, status, lastClinicalAt(patient));
  }

  private Patient patient(Long id) {
    return patients.findById(id).orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
  }

  private Integer age(Patient patient) {
    return patient.getBirthDate() == null ? null : Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
  }

  private Instant lastClinicalAt(Patient patient) {
    return Stream.of(
        glucose.findFirstByPatientIdOrderByMeasuredAtDesc(patient.getId()).map(GlucoseMeasurement::getMeasuredAt).orElse(null),
        meals.findFirstByPatientIdOrderByEatenAtDesc(patient.getId()).map(Meal::getEatenAt).orElse(null),
        alerts.findFirstByPatientIdOrderByOccurredAtDesc(patient.getId()).map(Alert::getOccurredAt).orElse(null))
      .filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
  }
}

