package com.example.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.myapp.entity.MRItemPrescription;

@Repository
public interface MRItemPrescriptionRepository extends JpaRepository<MRItemPrescription, Long> {
}

