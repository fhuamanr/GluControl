package com.glucontrol.repository;
import com.glucontrol.entity.GlucoseMeasurement;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface GlucoseMeasurementRepository extends JpaRepository<GlucoseMeasurement,Long> { Page<GlucoseMeasurement> findByPatientIdOrderByMeasuredAtDesc(Long patientId,Pageable pageable); List<GlucoseMeasurement> findTop7ByPatientIdOrderByMeasuredAtDesc(Long patientId); }

