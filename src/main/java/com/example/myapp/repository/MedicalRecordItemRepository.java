package com.example.myapp.repository;

import com.example.myapp.entity.MedicalRecordItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordItemRepository extends JpaRepository<MedicalRecordItem, Integer> {
}
