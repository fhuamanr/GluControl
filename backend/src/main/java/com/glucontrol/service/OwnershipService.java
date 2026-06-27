package com.glucontrol.service;

import com.glucontrol.entity.Patient;
import com.glucontrol.entity.User;
import com.glucontrol.exception.ResourceNotFoundException;
import com.glucontrol.repository.PatientRepository;
import com.glucontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnershipService {
  private final UserRepository users;
  private final PatientRepository patients;

  public void requireReadPatient(Long patientId) {
    User user = currentUser();
    if (user.getRole() != User.Role.PATIENT) return;
    Patient patient = patient(patientId);
    if (!patient.getUser().getId().equals(user.getId())) throw new AccessDeniedException("Paciente no autorizado");
  }

  public void requireWritePatient(Long patientId) {
    User user = currentUser();
    if (user.getRole() == User.Role.ADMIN) return;
    Patient patient = patient(patientId);
    if (user.getRole() != User.Role.PATIENT || !patient.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("No puedes modificar información de otro paciente");
    }
  }

  public void requireOwnUser(Long userId) {
    User user = currentUser();
    if (user.getRole() != User.Role.ADMIN && !user.getId().equals(userId)) throw new AccessDeniedException("Usuario no autorizado");
  }

  public User currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) throw new AccessDeniedException("Sesión requerida");
    return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
  }

  private Patient patient(Long id) {
    return patients.findById(id).orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
  }
}

