package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;
import com.example.hospital_api.entity.Allergy.AllergySeverity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Integer> {
    List<Allergy> findByName(String name);
    List<Allergy> findBySeverity(AllergySeverity severity);
}