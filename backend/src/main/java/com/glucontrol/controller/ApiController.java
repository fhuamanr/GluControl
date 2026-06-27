package com.glucontrol.controller;

import com.glucontrol.dto.*;
import com.glucontrol.service.ClinicalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.*;

@RestController @RequiredArgsConstructor
public class ApiController {
  private final ClinicalService service; private final DataSource dataSource;
  @GetMapping("/api/health") public Map<String,Object> health() throws Exception { try(Connection ignored=dataSource.getConnection()){return Map.of("status","UP","database","UP","timestamp",Instant.now());} }
  @PostMapping("/api/auth/login") public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest d){return service.login(d);}
  @GetMapping("/api/patients") public Page<PatientDto> patients(@RequestParam(required=false)String q,@PageableDefault(size=20,sort="id")Pageable p){return service.patientList(q,p);}
  @GetMapping("/api/patients/{id}") public PatientDto patient(@PathVariable Long id){return service.patientGet(id);}
  @PostMapping("/api/patients") @ResponseStatus(HttpStatus.CREATED) public PatientDto patientCreate(@Valid @RequestBody PatientDto d){return service.patientCreate(d);}
  @PutMapping("/api/patients/{id}") public PatientDto patientUpdate(@PathVariable Long id,@Valid @RequestBody PatientDto d){return service.patientUpdate(id,d);}
  @DeleteMapping("/api/patients/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void patientDelete(@PathVariable Long id){service.patientDelete(id);}
  @GetMapping("/api/patients/{id}/glucose") public Page<GlucoseDto> glucose(@PathVariable Long id,@PageableDefault(size=20)Pageable p){return service.glucoseList(id,p);}
  @PostMapping("/api/glucose") @ResponseStatus(HttpStatus.CREATED) public GlucoseDto glucoseCreate(@Valid @RequestBody GlucoseDto d){return service.glucoseCreate(d);}
  @PutMapping("/api/glucose/{id}") public GlucoseDto glucoseUpdate(@PathVariable Long id,@Valid @RequestBody GlucoseDto d){return service.glucoseUpdate(id,d);}
  @DeleteMapping("/api/glucose/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void glucoseDelete(@PathVariable Long id){service.glucoseDelete(id);}
  @GetMapping("/api/patients/{id}/meals") public Page<MealDto> meals(@PathVariable Long id,@PageableDefault(size=20)Pageable p){return service.mealList(id,p);}
  @PostMapping("/api/meals") @ResponseStatus(HttpStatus.CREATED) public MealDto mealCreate(@Valid @RequestBody MealDto d){return service.mealSave(d);}
  @PutMapping("/api/meals/{id}") public MealDto mealUpdate(@PathVariable Long id,@Valid @RequestBody MealDto d){return service.mealSave(new MealDto(id,d.patientId(),d.name(),d.mealType(),d.eatenAt(),d.carbohydratesGrams(),d.calories(),d.photoUrl(),d.notes()));}
  @DeleteMapping("/api/meals/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void mealDelete(@PathVariable Long id){service.mealDelete(id);}
  @GetMapping("/api/patients/{id}/medications") public List<MedicationDto> medications(@PathVariable Long id){return service.medicationList(id);}
  @PostMapping("/api/medications") @ResponseStatus(HttpStatus.CREATED) public MedicationDto medicationCreate(@Valid @RequestBody MedicationDto d){return service.medicationSave(d);}
  @PutMapping("/api/medications/{id}") public MedicationDto medicationUpdate(@PathVariable Long id,@Valid @RequestBody MedicationDto d){return service.medicationSave(new MedicationDto(id,d.patientId(),d.name(),d.dose(),d.frequency(),d.reminderTime(),d.startDate(),d.endDate(),d.active(),d.instructions()));}
  @DeleteMapping("/api/medications/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void medicationDelete(@PathVariable Long id){service.medicationDelete(id);}
  @GetMapping("/api/patients/{id}/alerts") public Page<AlertDto> alerts(@PathVariable Long id,@PageableDefault(size=20)Pageable p){return service.alertList(id,p);}
  @PostMapping("/api/alerts") @ResponseStatus(HttpStatus.CREATED) public AlertDto alertCreate(@Valid @RequestBody AlertDto d){return service.alertSave(d);}
  @PutMapping("/api/alerts/{id}") public AlertDto alertUpdate(@PathVariable Long id,@Valid @RequestBody AlertDto d){return service.alertSave(new AlertDto(id,d.patientId(),d.type(),d.severity(),d.title(),d.message(),d.occurredAt(),d.acknowledged()));}
  @PatchMapping("/api/alerts/{id}/acknowledge") public AlertDto acknowledge(@PathVariable Long id){return service.acknowledge(id);}
  @DeleteMapping("/api/alerts/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void alertDelete(@PathVariable Long id){service.alertDelete(id);}
  @GetMapping("/api/patients/{id}/reports") public List<ReportDto> reports(@PathVariable Long id){return service.reportList(id);}
  @PostMapping("/api/reports") @ResponseStatus(HttpStatus.CREATED) public ReportDto report(@Valid @RequestBody ReportDto d){return service.reportSave(d);}
  @DeleteMapping("/api/reports/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void reportDelete(@PathVariable Long id){service.reportDelete(id);}
  @GetMapping("/api/users/{id}/settings") public SettingsDto settings(@PathVariable Long id){return service.settingsGet(id);}
  @PutMapping("/api/users/{id}/settings") public SettingsDto settingsUpdate(@PathVariable Long id,@Valid @RequestBody SettingsDto d){return service.settingsUpdate(id,d);}
  @GetMapping("/api/doctor/dashboard") public DashboardDto dashboard(){return service.dashboard();}
}
