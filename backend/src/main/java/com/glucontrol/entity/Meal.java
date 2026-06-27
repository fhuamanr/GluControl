package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="meals") @Getter @Setter @NoArgsConstructor
public class Meal extends BaseEntity {
  @ManyToOne(optional=false) @JoinColumn(name="patient_id") private Patient patient;
  @Column(nullable=false) private String name;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private MealType mealType;
  @Column(nullable=false) private Instant eatenAt;
  private BigDecimal carbohydratesGrams;
  private Integer calories;
  private String photoUrl;
  private String notes;
  public enum MealType { BREAKFAST, LUNCH, DINNER, SNACK }
}

