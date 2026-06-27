package com.glucontrol.dto;
import com.glucontrol.entity.Meal.MealType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
public record MealDto(Long id, @NotNull Long patientId, @NotBlank String name, @NotNull MealType mealType,
 @NotNull Instant eatenAt, @DecimalMin("0") BigDecimal carbohydratesGrams, @Min(0) Integer calories,
 String photoUrl, @Size(max=500) String notes) {}

