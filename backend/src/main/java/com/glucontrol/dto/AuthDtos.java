package com.glucontrol.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public final class AuthDtos {
  private AuthDtos() {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  public record RegisterRequest(@NotBlank String firstName, @NotBlank String lastName, @Email @NotBlank String email,
    @Size(min=8,max=72) String password, @Past LocalDate birthDate, String phone, String diabetesType) {}
  public record LoginResponse(Long userId, Long patientId, String fullName, String email, String role, String token) {}
}
