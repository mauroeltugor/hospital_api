package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
    Specialty findByName(String name);
}