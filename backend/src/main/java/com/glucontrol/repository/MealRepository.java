package com.glucontrol.repository;
import com.glucontrol.entity.Meal;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MealRepository extends JpaRepository<Meal,Long> {
  Page<Meal> findByPatientIdOrderByEatenAtDesc(Long patientId,Pageable pageable);
  Optional<Meal> findFirstByPatientIdOrderByEatenAtDesc(Long patientId);
}
