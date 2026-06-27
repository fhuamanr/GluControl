package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity @Table(name="medications") @Getter @Setter @NoArgsConstructor
public class Medication extends BaseEntity {
  @ManyToOne(optional=false) @JoinColumn(name="patient_id") private Patient patient;
  @Column(nullable=false) private String name;
  @Column(nullable=false) private String dose;
  private String frequency;
  private LocalTime reminderTime;
  private LocalDate startDate;
  private LocalDate endDate;
  @Column(nullable=false) private boolean active = true;
  private String instructions;
}

