package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name="medical_reports") @Getter @Setter @NoArgsConstructor
public class MedicalReport extends BaseEntity {
  @ManyToOne(optional=false) @JoinColumn(name="patient_id") private Patient patient;
  @Column(nullable=false) private LocalDate periodStart;
  @Column(nullable=false) private LocalDate periodEnd;
  private Integer averageGlucose;
  private Integer readingsInRangePercent;
  @Column(length=2000) private String summary;
  private String documentUrl;
}

