package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;
import com.example.hospital_api.entity.DoctorSchedule.WorkDay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Integer> {
    List<DoctorSchedule> findByDoctorId(int doctorId);
    List<DoctorSchedule> findByWorkDay(WorkDay workDay);
}