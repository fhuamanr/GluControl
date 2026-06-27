package com.glucontrol.dto;
import jakarta.validation.constraints.NotBlank;
public record SettingsDto(Long id, Long userId, boolean glucoseAlerts, boolean medicationReminders,
 boolean mealReminders, @NotBlank String locale, @NotBlank String timezone) {}

