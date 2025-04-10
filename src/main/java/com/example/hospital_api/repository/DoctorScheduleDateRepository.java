package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;
import com.example.hospital_api.entity.DoctorScheduleDate.ScheduleStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DoctorScheduleDateRepository extends JpaRepository<DoctorScheduleDate, Integer> {
    List<DoctorScheduleDate> findByScheduleId(int scheduleId);
    List<DoctorScheduleDate> findByStatus(ScheduleStatus status);
    List<DoctorScheduleDate> findByDateBetween(Date startDate, Date endDate);
}