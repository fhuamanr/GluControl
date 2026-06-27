package com.glucontrol.service;

import com.glucontrol.dto.*;
import com.glucontrol.entity.*;
import com.glucontrol.exception.ResourceNotFoundException;
import com.glucontrol.mapper.ApiMapper;
import com.glucontrol.repository.*;
import com.glucontrol.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class ClinicalService {
  private final UserRepository users; private final PatientRepository patients; private final GlucoseMeasurementRepository glucose;
  private final MealRepository meals; private final MedicationRepository medications; private final AlertRepository alerts;
  private final MedicalReportRepository reports; private final UserSettingsRepository settings; private final ApiMapper mapper;
  private final BCryptPasswordEncoder encoder;
  private final JwtService jwt;
  private final OwnershipService access;
  private final DoctorService doctorService;

  @Transactional(readOnly=true) public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
    User u=users.findByEmailIgnoreCase(req.email()).orElseThrow(()->new IllegalArgumentException("Credenciales incorrectas"));
    if (!u.isActive() || !encoder.matches(req.password(),u.getPasswordHash())) throw new IllegalArgumentException("Credenciales incorrectas");
    Long patientId=patients.findAll().stream().filter(p->p.getUser().getId().equals(u.getId())).map(Patient::getId).findFirst().orElse(null);
    return new AuthDtos.LoginResponse(u.getId(),patientId,u.getFullName(),u.getEmail(),u.getRole().name(),jwt.issue(u));
  }
  private Patient patient(Long id) { return patients.findById(id).orElseThrow(()->new ResourceNotFoundException("Paciente no encontrado")); }
  @Transactional(readOnly=true) public Page<PatientDto> patientList(String q,Pageable p) { User u=access.currentUser();if(u.getRole()==User.Role.PATIENT){List<PatientDto> own=patients.findByUserEmailIgnoreCase(u.getEmail()).map(mapper::patient).stream().toList();return new PageImpl<>(own,p,own.size());}return (q==null||q.isBlank()?patients.findByUserRole(User.Role.PATIENT,p):patients.findByUserRoleAndUserFullNameContainingIgnoreCase(User.Role.PATIENT,q,p)).map(mapper::patient); }
  @Transactional(readOnly=true) public PatientDto patientGet(Long id) { access.requireReadPatient(id);return mapper.patient(patient(id)); }
  public PatientDto patientCreate(PatientDto d) { if(access.currentUser().getRole()!=User.Role.ADMIN)throw new org.springframework.security.access.AccessDeniedException("Solo administración puede crear pacientes");if(d.email()==null||d.fullName()==null)throw new IllegalArgumentException("Nombre y correo son obligatorios");User u=new User();u.setFullName(d.fullName());u.setEmail(d.email());u.setPasswordHash(encoder.encode(UUID.randomUUID().toString()));u.setRole(User.Role.PATIENT);u=users.save(u);Patient p=new Patient();p.setUser(u);p.setDocumentNumber(d.documentNumber());p.setBirthDate(d.birthDate());p.setPhone(d.phone());p.setDiabetesType(d.diabetesType());p.setGlucoseTargetMin(d.glucoseTargetMin());p.setGlucoseTargetMax(d.glucoseTargetMax());p.setEmergencyContact(d.emergencyContact());return mapper.patient(patients.save(p)); }
  public PatientDto patientUpdate(Long id,PatientDto d) { access.requireWritePatient(id);Patient p=patient(id); p.setDocumentNumber(d.documentNumber());p.setBirthDate(d.birthDate());p.setPhone(d.phone());p.setDiabetesType(d.diabetesType());p.setGlucoseTargetMin(d.glucoseTargetMin());p.setGlucoseTargetMax(d.glucoseTargetMax());p.setEmergencyContact(d.emergencyContact());return mapper.patient(p); }
  public void patientDelete(Long id) { if(access.currentUser().getRole()!=User.Role.ADMIN)throw new org.springframework.security.access.AccessDeniedException("Solo administración puede eliminar pacientes");patients.delete(patient(id)); }

  @Transactional(readOnly=true) public Page<GlucoseDto> glucoseList(Long patientId,Pageable p) { access.requireReadPatient(patientId);patient(patientId); return glucose.findByPatientIdOrderByMeasuredAtDesc(patientId,p).map(mapper::glucose); }
  public GlucoseDto glucoseCreate(GlucoseDto d) { GlucoseMeasurement g=new GlucoseMeasurement(); glucoseFields(g,d); g=glucose.save(g); createGlucoseAlert(g); return mapper.glucose(g); }
  public GlucoseDto glucoseUpdate(Long id,GlucoseDto d) { GlucoseMeasurement g=glucose.findById(id).orElseThrow(()->new ResourceNotFoundException("Medición no encontrada"));access.requireWritePatient(g.getPatient().getId()); glucoseFields(g,d); return mapper.glucose(g); }
  public void glucoseDelete(Long id) { GlucoseMeasurement g=glucose.findById(id).orElseThrow(()->new ResourceNotFoundException("Medición no encontrada"));access.requireWritePatient(g.getPatient().getId());glucose.delete(g); }
  private void glucoseFields(GlucoseMeasurement g,GlucoseDto d) { access.requireWritePatient(d.patientId());g.setPatient(patient(d.patientId()));g.setValueMgDl(d.valueMgDl());g.setMeasuredAt(d.measuredAt());g.setContext(d.context());g.setNotes(d.notes()); }
  private void createGlucoseAlert(GlucoseMeasurement g) { if(g.getValueMgDl()>=70&&g.getValueMgDl()<=180)return; Alert a=new Alert();a.setPatient(g.getPatient());a.setType(g.getValueMgDl()<70?Alert.AlertType.LOW_GLUCOSE:Alert.AlertType.HIGH_GLUCOSE);a.setSeverity(g.getValueMgDl()<54||g.getValueMgDl()>250?Alert.Severity.CRITICAL:Alert.Severity.WARNING);a.setTitle(g.getValueMgDl()<70?"Glucosa baja":"Glucosa elevada");a.setMessage("Lectura de "+g.getValueMgDl()+" mg/dL. Sigue tu plan de cuidado.");a.setOccurredAt(g.getMeasuredAt());alerts.save(a); }

  @Transactional(readOnly=true) public Page<MealDto> mealList(Long id,Pageable p){access.requireReadPatient(id);patient(id);return meals.findByPatientIdOrderByEatenAtDesc(id,p).map(mapper::meal);}
  public MealDto mealSave(MealDto d){Meal m=d.id()==null?new Meal():meals.findById(d.id()).orElseThrow(()->new ResourceNotFoundException("Comida no encontrada"));if(m.getId()!=null)access.requireWritePatient(m.getPatient().getId());access.requireWritePatient(d.patientId());m.setPatient(patient(d.patientId()));m.setName(d.name());m.setMealType(d.mealType());m.setEatenAt(d.eatenAt());m.setCarbohydratesGrams(d.carbohydratesGrams());m.setCalories(d.calories());m.setPhotoUrl(d.photoUrl());m.setNotes(d.notes());return mapper.meal(meals.save(m));}
  public void mealDelete(Long id){Meal m=meals.findById(id).orElseThrow(()->new ResourceNotFoundException("Comida no encontrada"));access.requireWritePatient(m.getPatient().getId());meals.delete(m);}
  @Transactional(readOnly=true) public List<MedicationDto> medicationList(Long id){access.requireReadPatient(id);patient(id);return medications.findByPatientIdOrderByActiveDescNameAsc(id).stream().map(mapper::medication).toList();}
  public MedicationDto medicationSave(MedicationDto d){Medication m=d.id()==null?new Medication():medications.findById(d.id()).orElseThrow(()->new ResourceNotFoundException("Medicamento no encontrado"));if(m.getId()!=null)access.requireWritePatient(m.getPatient().getId());access.requireWritePatient(d.patientId());m.setPatient(patient(d.patientId()));m.setName(d.name());m.setDose(d.dose());m.setFrequency(d.frequency());m.setReminderTime(d.reminderTime());m.setStartDate(d.startDate());m.setEndDate(d.endDate());m.setActive(d.active());m.setInstructions(d.instructions());return mapper.medication(medications.save(m));}
  public void medicationDelete(Long id){Medication m=medications.findById(id).orElseThrow(()->new ResourceNotFoundException("Medicamento no encontrado"));access.requireWritePatient(m.getPatient().getId());medications.delete(m);}
  @Transactional(readOnly=true) public Page<AlertDto> alertList(Long id,Pageable p){access.requireReadPatient(id);patient(id);return alerts.findByPatientIdOrderByOccurredAtDesc(id,p).map(mapper::alert);}
  public AlertDto alertSave(AlertDto d){Alert a=d.id()==null?new Alert():alerts.findById(d.id()).orElseThrow(()->new ResourceNotFoundException("Alerta no encontrada"));if(a.getId()!=null)access.requireWritePatient(a.getPatient().getId());access.requireWritePatient(d.patientId());a.setPatient(patient(d.patientId()));a.setType(d.type());a.setSeverity(d.severity());a.setTitle(d.title());a.setMessage(d.message());a.setOccurredAt(d.occurredAt());a.setAcknowledged(d.acknowledged());return mapper.alert(alerts.save(a));}
  public AlertDto acknowledge(Long id){Alert a=alerts.findById(id).orElseThrow(()->new ResourceNotFoundException("Alerta no encontrada"));access.requireWritePatient(a.getPatient().getId());a.setAcknowledged(true);return mapper.alert(a);}
  public void alertDelete(Long id){Alert a=alerts.findById(id).orElseThrow(()->new ResourceNotFoundException("Alerta no encontrada"));access.requireWritePatient(a.getPatient().getId());alerts.delete(a);}
  @Transactional(readOnly=true) public List<ReportDto> reportList(Long id){access.requireReadPatient(id);patient(id);return reports.findByPatientIdOrderByPeriodEndDesc(id).stream().map(mapper::report).toList();}
  public ReportDto reportSave(ReportDto d){access.requireWritePatient(d.patientId());MedicalReport r=new MedicalReport();r.setPatient(patient(d.patientId()));r.setPeriodStart(d.periodStart());r.setPeriodEnd(d.periodEnd());r.setAverageGlucose(d.averageGlucose());r.setReadingsInRangePercent(d.readingsInRangePercent());r.setSummary(d.summary());r.setDocumentUrl(d.documentUrl());return mapper.report(reports.save(r));}
  public void reportDelete(Long id){MedicalReport r=reports.findById(id).orElseThrow(()->new ResourceNotFoundException("Reporte no encontrado"));access.requireWritePatient(r.getPatient().getId());reports.delete(r);}
  @Transactional(readOnly=true) public SettingsDto settingsGet(Long userId){access.requireOwnUser(userId);return settings.findByUserId(userId).map(mapper::settings).orElseThrow(()->new ResourceNotFoundException("Configuración no encontrada"));}
  public SettingsDto settingsUpdate(Long userId,SettingsDto d){access.requireOwnUser(userId);UserSettings s=settings.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("Configuración no encontrada"));s.setGlucoseAlerts(d.glucoseAlerts());s.setMedicationReminders(d.medicationReminders());s.setMealReminders(d.mealReminders());s.setLocale(d.locale());s.setTimezone(d.timezone());return mapper.settings(s);}
  @Transactional(readOnly=true) public DashboardDto dashboard(){return doctorService.dashboard();}
}
