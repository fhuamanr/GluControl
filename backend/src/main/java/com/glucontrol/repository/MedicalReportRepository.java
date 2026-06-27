package com.glucontrol.repository;
import com.glucontrol.entity.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MedicalReportRepository extends JpaRepository<MedicalReport,Long> { List<MedicalReport> findByPatientIdOrderByPeriodEndDesc(Long patientId); }

