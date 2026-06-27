package com.glucontrol.service;

import com.glucontrol.config.JwtService;
import com.glucontrol.dto.AuthDtos;
import com.glucontrol.entity.Patient;
import com.glucontrol.entity.User;
import com.glucontrol.entity.UserSettings;
import com.glucontrol.repository.PatientRepository;
import com.glucontrol.repository.UserRepository;
import com.glucontrol.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
  private final UserRepository users;
  private final PatientRepository patients;
  private final UserSettingsRepository settings;
  private final BCryptPasswordEncoder encoder;
  private final JwtService jwt;

  @Transactional(readOnly = true)
  public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
    User user = users.findByEmailIgnoreCase(request.email())
      .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));
    if (!user.isActive() || !encoder.matches(request.password(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Credenciales incorrectas");
    }
    return session(user);
  }

  public AuthDtos.LoginResponse register(AuthDtos.RegisterRequest request) {
    if (users.existsByEmailIgnoreCase(request.email())) {
      throw new IllegalArgumentException("Ya existe una cuenta con ese correo");
    }
    User user = new User();
    user.setFullName((request.firstName().trim() + " " + request.lastName().trim()).trim());
    user.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
    user.setPasswordHash(encoder.encode(request.password()));
    user.setRole(User.Role.PATIENT);
    user = users.save(user);

    Patient patient = new Patient();
    patient.setUser(user);
    patient.setDocumentNumber("AUTO-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT));
    patient.setBirthDate(request.birthDate());
    patient.setPhone(blankToNull(request.phone()));
    patient.setDiabetesType(blankToNull(request.diabetesType()));
    patient.setGlucoseTargetMin(70);
    patient.setGlucoseTargetMax(180);
    patients.save(patient);

    UserSettings preferences = new UserSettings();
    preferences.setUser(user);
    settings.save(preferences);
    return session(user);
  }

  private AuthDtos.LoginResponse session(User user) {
    Long patientId = patients.findByUserEmailIgnoreCase(user.getEmail()).map(Patient::getId).orElse(null);
    return new AuthDtos.LoginResponse(user.getId(), patientId, user.getFullName(), user.getEmail(),
      user.getRole().name(), jwt.issue(user));
  }

  private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}

