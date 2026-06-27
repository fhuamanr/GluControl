package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name="patients") @Getter @Setter @NoArgsConstructor
public class Patient extends BaseEntity {
  @OneToOne(optional=false) @JoinColumn(name="user_id", unique=true) private User user;
  @Column(nullable=false, unique=true) private String documentNumber;
  private LocalDate birthDate;
  private String phone;
  private String diabetesType;
  private Integer glucoseTargetMin;
  private Integer glucoseTargetMax;
  private String emergencyContact;
}

