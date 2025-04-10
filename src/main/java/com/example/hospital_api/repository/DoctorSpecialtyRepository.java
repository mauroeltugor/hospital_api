package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;
import com.example.hospital_api.entity.DoctorSpecialty.ExperienceLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorSpecialtyRepository extends JpaRepository<DoctorSpecialty, Integer> {
    List<DoctorSpecialty> findByDoctorId(int doctorId);
    List<DoctorSpecialty> findBySpecialtyId(int specialtyId);
    List<DoctorSpecialty> findByExperienceLevel(ExperienceLevel experienceLevel);
}