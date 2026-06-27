package com.glucontrol.service;

import com.glucontrol.config.JwtService;
import com.glucontrol.dto.AuthDtos;
import com.glucontrol.entity.Patient;
import com.glucontrol.entity.User;
import com.glucontrol.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
  @Test void registerCreatesPatientUserAndSettings() {
    UserRepository users=mock(UserRepository.class);PatientRepository patients=mock(PatientRepository.class);
    UserSettingsRepository settings=mock(UserSettingsRepository.class);BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);
    JwtService jwt=mock(JwtService.class);AtomicReference<Patient> stored=new AtomicReference<>();
    when(users.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
    when(encoder.encode("segura123")).thenReturn("hash");
    when(users.save(any(User.class))).thenAnswer(call->{User user=call.getArgument(0);user.setId(10L);return user;});
    when(patients.save(any(Patient.class))).thenAnswer(call->{Patient patient=call.getArgument(0);patient.setId(20L);stored.set(patient);return patient;});
    when(patients.findByUserEmailIgnoreCase("ana@example.com")).thenAnswer(call->Optional.of(stored.get()));
    when(jwt.issue(any(User.class))).thenReturn("jwt-ana");
    AuthService service=new AuthService(users,patients,settings,encoder,jwt);

    var result=service.register(new AuthDtos.RegisterRequest("Ana","Torres","ANA@example.com","segura123", LocalDate.of(1990,1,1),"999111222","Tipo 2"));

    assertThat(result.role()).isEqualTo("PATIENT");assertThat(result.patientId()).isEqualTo(20L);
    assertThat(stored.get().getUser().getRole()).isEqualTo(User.Role.PATIENT);
    assertThat(stored.get().getDocumentNumber()).startsWith("AUTO-");
    verify(settings).save(argThat(value->value.getUser().getId().equals(10L)));
  }

  @Test void duplicateEmailIsRejected() {
    UserRepository users=mock(UserRepository.class);when(users.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);
    AuthService service=new AuthService(users,mock(PatientRepository.class),mock(UserSettingsRepository.class),mock(BCryptPasswordEncoder.class),mock(JwtService.class));
    assertThatThrownBy(()->service.register(new AuthDtos.RegisterRequest("Ana","Torres","ana@example.com","segura123",null,null,null)))
      .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Ya existe");
    verify(users,never()).save(any());
  }
}

