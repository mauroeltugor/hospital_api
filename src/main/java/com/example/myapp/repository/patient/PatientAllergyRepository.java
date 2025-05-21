package com.example.myapp.repository.patient;

import com.example.myapp.entity.PatientAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientAllergyRepository extends JpaRepository<PatientAllergy, Integer> {
}
