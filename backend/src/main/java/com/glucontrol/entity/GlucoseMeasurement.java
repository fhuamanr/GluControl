package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="glucose_measurements") @Getter @Setter @NoArgsConstructor
public class GlucoseMeasurement extends BaseEntity {
  @ManyToOne(optional=false) @JoinColumn(name="patient_id") private Patient patient;
  @Column(nullable=false) private Integer valueMgDl;
  @Column(nullable=false) private Instant measuredAt;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Context context;
  private String notes;
  public enum Context { FASTING, BEFORE_MEAL, AFTER_MEAL, BEDTIME, OTHER }
}

