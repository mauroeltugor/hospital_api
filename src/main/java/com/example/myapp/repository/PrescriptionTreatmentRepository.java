package com.example.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.myapp.entity.PrescriptionTreatment;

@Repository
public interface PrescriptionTreatmentRepository extends JpaRepository<PrescriptionTreatment, Long> {
}

