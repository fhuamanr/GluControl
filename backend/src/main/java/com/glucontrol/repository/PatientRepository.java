package com.glucontrol.repository;
import com.glucontrol.entity.Patient;
import com.glucontrol.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PatientRepository extends JpaRepository<Patient,Long> {
  Page<Patient> findByUserRole(User.Role role, Pageable pageable);
  Page<Patient> findByUserRoleAndUserFullNameContainingIgnoreCase(User.Role role, String name, Pageable pageable);
  Optional<Patient> findByUserEmailIgnoreCase(String email);
}
