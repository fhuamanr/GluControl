package com.glucontrol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor
public class User extends BaseEntity {
  @Column(nullable=false) private String fullName;
  @Column(nullable=false, unique=true) private String email;
  @Column(nullable=false) private String passwordHash;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
  @Column(nullable=false) private boolean active = true;
  public enum Role { PATIENT, DOCTOR, ADMIN }
}

