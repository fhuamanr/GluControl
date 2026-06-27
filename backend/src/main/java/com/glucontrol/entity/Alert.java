package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="alerts") @Getter @Setter @NoArgsConstructor
public class Alert extends BaseEntity {
  @ManyToOne(optional=false) @JoinColumn(name="patient_id") private Patient patient;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private AlertType type;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Severity severity;
  @Column(nullable=false) private String title;
  @Column(nullable=false, length=1000) private String message;
  @Column(nullable=false) private Instant occurredAt;
  @Column(nullable=false) private boolean acknowledged;
  public enum AlertType { LOW_GLUCOSE, HIGH_GLUCOSE, MEDICATION, MEAL, SYSTEM }
  public enum Severity { INFO, WARNING, CRITICAL }
}

