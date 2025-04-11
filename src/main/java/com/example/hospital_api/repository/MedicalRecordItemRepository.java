package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface MedicalRecordItemRepository extends JpaRepository<MedicalRecordItem, Integer> {
    List<MedicalRecordItem> findByMedicalRecordId(int medicalRecordId);
    List<MedicalRecordItem> findBySessionId(int sessionId);
    List<MedicalRecordItem> findByEntryDateBetween(LocalDateTime start, LocalDateTime end);
}