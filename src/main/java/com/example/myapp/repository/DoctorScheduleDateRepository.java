package com.example.myapp.repository;

import com.example.myapp.entity.DoctorScheduleDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorScheduleDateRepository extends JpaRepository<DoctorScheduleDate, Integer> {
}
