package com.example.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.myapp.entity.SpecialtyPrescription;

@Repository
public interface SpecialtyPrescriptionRepository extends JpaRepository<SpecialtyPrescription, Long> {
}
