package com.glucontrol.repository;
import com.glucontrol.entity.Meal;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MealRepository extends JpaRepository<Meal,Long> { Page<Meal> findByPatientIdOrderByEatenAtDesc(Long patientId,Pageable pageable); }

