package com.example.myapp.repository;

import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByLicenseNumber(String licenseNumber);

    List<DoctorSchedule> findByDoctorId(Long doctorId);
}
