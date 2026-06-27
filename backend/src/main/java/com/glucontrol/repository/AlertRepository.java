package com.glucontrol.repository;
import com.glucontrol.entity.Alert;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AlertRepository extends JpaRepository<Alert,Long> {
  Page<Alert> findByPatientIdOrderByOccurredAtDesc(Long patientId,Pageable pageable);
  long countByAcknowledgedFalse();
  long countByAcknowledgedFalseAndSeverity(Alert.Severity severity);
  long countByPatientIdAndAcknowledgedFalse(Long patientId);
  Optional<Alert> findFirstByPatientIdOrderByOccurredAtDesc(Long patientId);
}
