package com.glucontrol.repository;
import com.glucontrol.entity.Patient;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PatientRepository extends JpaRepository<Patient,Long> { Page<Patient> findByUserFullNameContainingIgnoreCase(String name, Pageable pageable); }

