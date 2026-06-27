package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="user_settings") @Getter @Setter @NoArgsConstructor
public class UserSettings extends BaseEntity {
  @OneToOne(optional=false) @JoinColumn(name="user_id", unique=true) private User user;
  @Column(nullable=false) private boolean glucoseAlerts = true;
  @Column(nullable=false) private boolean medicationReminders = true;
  @Column(nullable=false) private boolean mealReminders = true;
  @Column(nullable=false) private String locale = "es-PE";
  @Column(nullable=false) private String timezone = "America/Lima";
}

