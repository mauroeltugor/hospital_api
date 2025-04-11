package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    List<Prescription> findByMedicalRecordItemId(int medicalRecordItemId);
    List<Prescription> findByIssuedAtBetween(LocalDateTime start, LocalDateTime end);
}