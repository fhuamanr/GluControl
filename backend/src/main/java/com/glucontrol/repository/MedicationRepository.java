package com.glucontrol.repository;
import com.glucontrol.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MedicationRepository extends JpaRepository<Medication,Long> {
  List<Medication> findByPatientIdOrderByActiveDescNameAsc(Long patientId);
  long countByPatientIdAndActiveTrue(Long patientId);
}
