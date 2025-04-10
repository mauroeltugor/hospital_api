package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Integer> {
    List<Diagnosis> findByName(String name);
    List<Diagnosis> findByMedicalRecordItemId(int medicalRecordItemId);
    List<Diagnosis> findByIssuedAtBetween(LocalDateTime start, LocalDateTime end);
}
