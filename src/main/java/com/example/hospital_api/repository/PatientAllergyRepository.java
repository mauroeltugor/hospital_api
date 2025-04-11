package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientAllergyRepository extends JpaRepository<PatientAllergy, Integer> {
    List<PatientAllergy> findByPatientId(int patientId);
    List<PatientAllergy> findByAllergyId(int allergyId);
}