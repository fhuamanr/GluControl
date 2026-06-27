package com.glucontrol.dto;
import jakarta.validation.constraints.*;
public final class AuthDtos {
  private AuthDtos() {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  public record LoginResponse(Long userId, Long patientId, String fullName, String email, String role, String token) {}
}
