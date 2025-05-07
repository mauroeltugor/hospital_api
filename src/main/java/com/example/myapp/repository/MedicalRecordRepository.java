package com.example.myapp.repository;

import com.example.myapp.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
}
